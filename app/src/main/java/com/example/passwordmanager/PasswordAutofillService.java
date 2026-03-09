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
import android.service.autofill.SaveRequest;
import android.text.InputType;
import android.view.View;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

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

        FillContext fillContext = request.getFillContexts()
                .get(request.getFillContexts().size() - 1);
        ParsedAutofillFields parsedFields = parseFields(fillContext.getStructure());
        if (parsedFields.usernameId == null && parsedFields.passwordId == null) {
            callback.onSuccess(null);
            return;
        }

        List<VaultItemEntity> items = LocalRepository.getInstance(this).getPasswordItemsForCurrentUser();
        FillResponse.Builder responseBuilder = new FillResponse.Builder();
        boolean hasDatasets = false;

        for (VaultItemEntity item : items) {
            if (!item.autofillEnabled) {
                continue;
            }

            RemoteViews presentation = new RemoteViews(getPackageName(),
                    android.R.layout.simple_list_item_1);
            presentation.setTextViewText(android.R.id.text1, item.name);

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

        callback.onSuccess(hasDatasets ? responseBuilder.build() : null);
    }

    @Override
    public void onSaveRequest(@NonNull SaveRequest request, @NonNull SaveCallback callback) {
        callback.onSuccess();
    }

    private ParsedAutofillFields parseFields(AssistStructure structure) {
        ParsedAutofillFields fields = new ParsedAutofillFields();
        for (int windowIndex = 0; windowIndex < structure.getWindowNodeCount(); windowIndex++) {
            AssistStructure.ViewNode root = structure.getWindowNodeAt(windowIndex).getRootViewNode();
            traverseNode(root, fields);
        }
        return fields;
    }

    private void traverseNode(AssistStructure.ViewNode node, ParsedAutofillFields fields) {
        if (node == null) {
            return;
        }

        if (fields.usernameId == null && isUsernameField(node)) {
            fields.usernameId = node.getAutofillId();
        }
        if (fields.passwordId == null && isPasswordField(node)) {
            fields.passwordId = node.getAutofillId();
        }

        for (int childIndex = 0; childIndex < node.getChildCount(); childIndex++) {
            traverseNode(node.getChildAt(childIndex), fields);
        }
    }

    private boolean isUsernameField(AssistStructure.ViewNode node) {
        return matchesHint(node, "username", "email", View.AUTOFILL_HINT_USERNAME,
                View.AUTOFILL_HINT_EMAIL_ADDRESS);
    }

    private boolean isPasswordField(AssistStructure.ViewNode node) {
        if (matchesHint(node, "password", View.AUTOFILL_HINT_PASSWORD)) {
            return true;
        }

        int inputType = node.getInputType();
        int variation = inputType & InputType.TYPE_MASK_VARIATION;
        return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD
                || variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
                || variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD;
    }

    private boolean matchesHint(AssistStructure.ViewNode node, String... values) {
        for (String value : values) {
            String lowerCaseValue = value.toLowerCase(Locale.US);
            if (containsIgnoreCase(node.getIdEntry(), lowerCaseValue)
                    || containsIgnoreCase(node.getHint(), lowerCaseValue)) {
                return true;
            }
        }

        String[] autofillHints = node.getAutofillHints();
        if (autofillHints == null) {
            return false;
        }

        for (String autofillHint : autofillHints) {
            for (String value : values) {
                if (value.equalsIgnoreCase(autofillHint)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsIgnoreCase(CharSequence text, String expectedPart) {
        return text != null && text.toString().toLowerCase(Locale.US).contains(expectedPart);
    }

    private static class ParsedAutofillFields {
        private AutofillId usernameId;
        private AutofillId passwordId;
    }
}
