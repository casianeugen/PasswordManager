package com.example.passwordmanager;

import android.app.assist.AssistStructure;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveInfo;
import android.service.autofill.SaveRequest;
import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.view.ViewStructure;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PasswordAutofillService extends AutofillService {
    @Override
    public void onFillRequest(@NonNull FillRequest request, @NonNull CancellationSignal cancellationSignal,
                              @NonNull FillCallback callback) {
        if (cancellationSignal.isCanceled()) {
            callback.onSuccess(null);
            return;
        }

        if (request.getFillContexts().isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        FillContext fillContext = request.getFillContexts()
                .get(request.getFillContexts().size() - 1);
        ParsedAutofillFields parsedFields = parseFields(fillContext.getStructure());
        if (!parsedFields.hasCredentialTarget()) {
            callback.onSuccess(null);
            return;
        }

        SaveInfo saveInfo = buildSaveInfo(parsedFields);
        List<VaultItemEntity> items = LocalRepository.getInstance(this).getPasswordItemsForAutofill();
        List<VaultItemEntity> autofillItems = filterAutofillItems(items);
        FillResponse.Builder responseBuilder = new FillResponse.Builder();
        if (saveInfo != null) {
            responseBuilder.setSaveInfo(saveInfo);
        }

        if (autofillItems.isEmpty()) {
            callback.onSuccess(saveInfo == null ? null : responseBuilder.build());
            return;
        }

        boolean hasDatasets = false;

        for (VaultItemEntity item : autofillItems) {
            RemoteViews presentation = new RemoteViews(getPackageName(),
                    android.R.layout.simple_list_item_1);
            presentation.setTextViewText(android.R.id.text1, buildDatasetTitle(item));

            Dataset.Builder datasetBuilder = new Dataset.Builder(presentation);
            boolean hasValue = false;

            if (parsedFields.usernameId != null && item.username != null && !item.username.isEmpty()) {
                datasetBuilder.setValue(parsedFields.usernameId,
                        AutofillValue.forText(item.username), presentation);
                hasValue = true;
            }

            if (parsedFields.passwordId != null && item.password != null && !item.password.isEmpty()) {
                datasetBuilder.setValue(parsedFields.passwordId,
                        AutofillValue.forText(item.password), presentation);
                hasValue = true;
            }

            if (hasValue) {
                responseBuilder.addDataset(datasetBuilder.build());
                hasDatasets = true;
            }
        }

        callback.onSuccess(hasDatasets || saveInfo != null ? responseBuilder.build() : null);
    }

    @Override
    public void onSaveRequest(@NonNull SaveRequest request, @NonNull SaveCallback callback) {
        if (request.getFillContexts().isEmpty()) {
            callback.onSuccess();
            return;
        }

        FillContext fillContext = request.getFillContexts()
                .get(request.getFillContexts().size() - 1);
        ParsedAutofillFields parsedFields = parseFields(fillContext.getStructure());
        LocalRepository.getInstance(this).saveAutofillCredential(
                parsedFields.webDomain,
                parsedFields.appPackage,
                parsedFields.usernameValue,
                parsedFields.passwordValue
        );
        callback.onSuccess();
    }

    private ParsedAutofillFields parseFields(AssistStructure structure) {
        ParsedAutofillFields fields = new ParsedAutofillFields();
        if (structure.getActivityComponent() != null) {
            fields.appPackage = structure.getActivityComponent().getPackageName();
        }
        for (int windowIndex = 0; windowIndex < structure.getWindowNodeCount(); windowIndex++) {
            AssistStructure.ViewNode root = structure.getWindowNodeAt(windowIndex).getRootViewNode();
            traverseNode(root, fields);
        }
        fields.finalizeCandidates();
        return fields;
    }

    private void traverseNode(AssistStructure.ViewNode node, ParsedAutofillFields fields) {
        if (node == null) {
            return;
        }

        AutofillId autofillId = node.getAutofillId();
        boolean isEditableTextField = isEditableTextField(node);
        boolean passwordField = isPasswordField(node);
        String nodeText = getNodeText(node);

        if (fields.webDomain == null && node.getWebDomain() != null && !node.getWebDomain().trim().isEmpty()) {
            fields.webDomain = node.getWebDomain().trim();
        }

        if (isEditableTextField && fields.focusedId == null && node.isFocused()) {
            fields.focusedId = autofillId;
            fields.focusedPassword = passwordField;
            fields.focusedValue = nodeText;
        }

        if (isEditableTextField && !passwordField && fields.firstTextFieldId == null) {
            fields.firstTextFieldId = autofillId;
            fields.firstTextFieldValue = nodeText;
        }

        if (fields.usernameId == null && isUsernameField(node)) {
            fields.usernameId = autofillId;
            fields.usernameValue = nodeText;
        }
        if (fields.passwordId == null && passwordField) {
            fields.passwordId = autofillId;
            fields.passwordValue = nodeText;
        }

        for (int childIndex = 0; childIndex < node.getChildCount(); childIndex++) {
            traverseNode(node.getChildAt(childIndex), fields);
        }
    }

    private boolean isUsernameField(AssistStructure.ViewNode node) {
        if (matchesNodeMetadata(node, "username", "email", "login", "user", "account",
                "identifier", "phone", View.AUTOFILL_HINT_USERNAME,
                View.AUTOFILL_HINT_EMAIL_ADDRESS)) {
            return true;
        }

        int inputType = node.getInputType();
        int inputClass = inputType & InputType.TYPE_MASK_CLASS;
        return isEditableTextField(node)
                && inputClass == InputType.TYPE_CLASS_TEXT
                && (inputType & InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
    }

    private boolean isPasswordField(AssistStructure.ViewNode node) {
        if (matchesNodeMetadata(node, "password", "passcode", "pin", "pwd",
                View.AUTOFILL_HINT_PASSWORD)) {
            return true;
        }

        int inputType = node.getInputType();
        int variation = inputType & InputType.TYPE_MASK_VARIATION;
        return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD
                || variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
                || variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                || variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD;
    }

    private boolean isEditableTextField(AssistStructure.ViewNode node) {
        if (node.getAutofillId() == null) {
            return false;
        }

        int inputType = node.getInputType();
        int inputClass = inputType & InputType.TYPE_MASK_CLASS;
        if (inputClass == InputType.TYPE_CLASS_TEXT || inputClass == InputType.TYPE_CLASS_NUMBER) {
            return true;
        }

        CharSequence className = node.getClassName();
        return className != null
                && className.toString().toLowerCase(Locale.US).contains("edittext");
    }

    private boolean matchesNodeMetadata(AssistStructure.ViewNode node, String... values) {
        for (String value : values) {
            String lowerCaseValue = value.toLowerCase(Locale.US);
            if (containsIgnoreCase(node.getIdEntry(), lowerCaseValue)
                    || containsIgnoreCase(node.getHint(), lowerCaseValue)
                    || containsIgnoreCase(node.getText(), lowerCaseValue)
                    || containsIgnoreCase(node.getWebDomain(), lowerCaseValue)
                    || matchesHtmlInfo(node, lowerCaseValue)) {
                return true;
            }
        }

        String[] autofillHints = node.getAutofillHints();
        if (autofillHints != null) {
            for (String autofillHint : autofillHints) {
                for (String value : values) {
                    if (value.equalsIgnoreCase(autofillHint)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean matchesHtmlInfo(AssistStructure.ViewNode node, String expectedPart) {
        ViewStructure.HtmlInfo htmlInfo = node.getHtmlInfo();
        if (htmlInfo == null || htmlInfo.getAttributes() == null) {
            return false;
        }

        for (Pair<String, String> attribute : htmlInfo.getAttributes()) {
            if (containsIgnoreCase(attribute.first, expectedPart)
                    || containsIgnoreCase(attribute.second, expectedPart)) {
                return true;
            }
        }
        return false;
    }

    private List<VaultItemEntity> filterAutofillItems(List<VaultItemEntity> items) {
        List<VaultItemEntity> enabledItems = new ArrayList<>();
        for (VaultItemEntity item : items) {
            if (item.autofillEnabled) {
                enabledItems.add(item);
            }
        }

        if (!enabledItems.isEmpty()) {
            return enabledItems;
        }
        return items;
    }

    private String buildDatasetTitle(VaultItemEntity item) {
        if (item.username == null || item.username.trim().isEmpty()) {
            return item.name;
        }
        return item.name + " (" + item.username + ")";
    }

    private SaveInfo buildSaveInfo(ParsedAutofillFields fields) {
        List<AutofillId> ids = new ArrayList<>();
        if (fields.usernameId != null) {
            ids.add(fields.usernameId);
        }
        if (fields.passwordId != null) {
            ids.add(fields.passwordId);
        }
        if (ids.isEmpty()) {
            return null;
        }

        int dataType = 0;
        if (fields.usernameId != null) {
            dataType |= SaveInfo.SAVE_DATA_TYPE_USERNAME;
        }
        if (fields.passwordId != null) {
            dataType |= SaveInfo.SAVE_DATA_TYPE_PASSWORD;
        }
        return new SaveInfo.Builder(dataType, ids.toArray(new AutofillId[0]))
                .setFlags(SaveInfo.FLAG_SAVE_ON_ALL_VIEWS_INVISIBLE)
                .build();
    }

    private boolean containsIgnoreCase(CharSequence text, String expectedPart) {
        return text != null && text.toString().toLowerCase(Locale.US).contains(expectedPart);
    }

    private String getNodeText(AssistStructure.ViewNode node) {
        AutofillValue autofillValue = node.getAutofillValue();
        if (autofillValue != null && autofillValue.isText() && autofillValue.getTextValue() != null) {
            return autofillValue.getTextValue().toString();
        }
        if (node.getText() != null) {
            return node.getText().toString();
        }
        return null;
    }

    private static class ParsedAutofillFields {
        private AutofillId usernameId;
        private AutofillId passwordId;
        private AutofillId focusedId;
        private AutofillId firstTextFieldId;
        private boolean focusedPassword;
        private String usernameValue;
        private String passwordValue;
        private String focusedValue;
        private String firstTextFieldValue;
        private String webDomain;
        private String appPackage;

        private void finalizeCandidates() {
            if (usernameId == null && firstTextFieldId != null && !firstTextFieldId.equals(passwordId)) {
                usernameId = firstTextFieldId;
                usernameValue = firstTextFieldValue;
            }

            if (passwordId == null && focusedPassword) {
                passwordId = focusedId;
                passwordValue = focusedValue;
            } else if (usernameId == null && focusedId != null && !focusedPassword) {
                usernameId = focusedId;
                usernameValue = focusedValue;
            }
        }

        private boolean hasCredentialTarget() {
            return usernameId != null || passwordId != null;
        }
    }
}
