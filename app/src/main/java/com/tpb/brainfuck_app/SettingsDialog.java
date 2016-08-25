package com.tpb.brainfuck_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by theo on 23/08/16.
 */
public class SettingsDialog extends DialogFragment {
    private static final String TAG = "SettingsDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_settings, null);
        final TextInputLayout mNameWrapper = (TextInputLayout) view.findViewById(R.id.name_wrapper);
        final TextInputEditText mNameInput = (TextInputEditText) view.findViewById(R.id.name_input);
        final TextInputLayout mDescWrapper = (TextInputLayout) view.findViewById(R.id.desc_wrapper);
        final TextInputEditText mDescInput = (TextInputEditText) view.findViewById(R.id.desc_input);
        final TextInputEditText mSuffixInput = (TextInputEditText) view.findViewById(R.id.output_suffix_input);
        final TextInputEditText mSizeInput = (TextInputEditText) view.findViewById(R.id.size_input);
        final TextInputEditText mMaxInput = (TextInputEditText) view.findViewById(R.id.max_input);
        final TextInputEditText mMinInput = (TextInputEditText) view.findViewById(R.id.min_input);
        final CheckBox mWrapOverflowBox = (CheckBox) view.findViewById(R.id.overflow_checkbox);
        final CheckBox mWrapUnderflowBox = (CheckBox) view.findViewById(R.id.underflow_checkbox);
        final RadioGroup mMemoryOverflowGroup = (RadioGroup) view.findViewById(R.id.overflow_behaviour);
        final RadioGroup mMemoryUnderflowGroup = (RadioGroup) view.findViewById(R.id.underflow_behaviour);

        final Program program = getArguments().getParcelable("prog");
        if(program != null) {
            Log.i(TAG, "onCreateDialog: Program is not null");
            if(program.name != null && !program.name.isEmpty()) {
                mNameInput.setText(program.name);
            }
            if(program.desc != null && !program.desc.isEmpty()) {
                mDescInput.setText(program.desc);
            }
            if(!program.outputSuffix.isEmpty()) {
                mSuffixInput.setText(program.outputSuffix);
            }
            mSizeInput.setText(Integer.toString(program.defaultSize));
            mMaxInput.setText(Integer.toString(program.defaultMax));
            mMinInput.setText(Integer.toString(program.defaultMin));
            mWrapOverflowBox.setChecked(program.overflowBehaviour);
            mWrapUnderflowBox.setChecked(program.underflowBehaviour);
            ((RadioButton) mMemoryOverflowGroup.getChildAt(program.sizeOverflowBehaviour)).setChecked(true);
            ((RadioButton) mMemoryUnderflowGroup.getChildAt(program.sizeUnderflowBehaviour)).setChecked(true);
        }

        builder.setView(view);

        view.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;
                if(mNameInput.getText().toString().isEmpty()) {
                    error = true;
                    mNameWrapper.setError("Enter a name for the program");
                } else {
                    mNameInput.setError(null);
                }
                final int size = Integer.parseInt(mSizeInput.getText().toString());
                if(size < 10) {
                    error = true;
                    mSizeInput.setError("Memory size must be at least 10");
                } else {
                    mSizeInput.setError(null);
                }
                if(size > 100000) {
                    error = true;
                    mSizeInput.setError("Memory size must be less than 100000");
                } else {
                    mSizeInput.setError(null);
                }
                if(!error) {
                    SettingsDialog.this.getDialog().dismiss();
                }
            }
        });
        view.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsDialog.this.getDialog().cancel();
            }
        });




        return builder.create();
    }

}
