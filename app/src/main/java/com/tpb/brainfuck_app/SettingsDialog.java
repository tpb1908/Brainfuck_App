package com.tpb.brainfuck_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by theo on 23/08/16.
 */
public class SettingsDialog extends DialogFragment {
    private static final String TAG = "SettingsDialog";
    private static SettingsDialogListener mListener;
    private Program program;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            final Activity a = (Activity) context;
            try {
                mListener = (SettingsDialogListener) a;
            } catch(ClassCastException e) {
                throw new ClassCastException(a.toString() + " must implement SettingsDialogListener");
            }
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_settings, null);
        final TextInputLayout mNameWrapper = (TextInputLayout) view.findViewById(R.id.name_wrapper);
        final TextInputEditText mNameInput = (TextInputEditText) view.findViewById(R.id.name_input);
        final TextInputEditText mDescInput = (TextInputEditText) view.findViewById(R.id.desc_input);
        final TextInputEditText mSuffixInput = (TextInputEditText) view.findViewById(R.id.output_suffix_input);
        final TextInputEditText mSizeInput = (TextInputEditText) view.findViewById(R.id.size_input);
        final TextInputLayout mMaxWrapper = (TextInputLayout) view.findViewById(R.id.min_wrapper);
        final TextInputEditText mMaxInput = (TextInputEditText) view.findViewById(R.id.max_input);
        final TextInputLayout mMinWrapper = (TextInputLayout) view.findViewById(R.id.max_wrapper);
        final TextInputEditText mMinInput = (TextInputEditText) view.findViewById(R.id.min_input);
        final RadioGroup mPointerOverflowGroup = (RadioGroup) view.findViewById(R.id.pointer_overflow_behaviour);
        final RadioGroup mPointerUnderflowGroup = (RadioGroup) view.findViewById(R.id.pointer_underflow_behaviour);
        final RadioGroup mValueOverflowGroup = (RadioGroup) view.findViewById(R.id.value_overflow_behaviour);
        final RadioGroup mValueUnderflowGroup = (RadioGroup) view.findViewById(R.id.value_underflow_behaviour);

        mPointerOverflowGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.pointer_over_error_checkbox:
                        program.pointerOverflowBehaviour = 0;
                        break;
                    case R.id.pointer_over_wrap_checkbox:
                        program.pointerOverflowBehaviour = 1;
                        break;
                    case R.id.pointer_over_expand_checkbox:
                        program.pointerOverflowBehaviour = 2;
                        break;
                }
            }
        });
        mPointerUnderflowGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.pointer_under_error_checkbox:
                        program.pointerUnderflowBehaviour = 0;
                        break;
                    case R.id.pointer_under_wrap_checkbox:
                        program.pointerUnderflowBehaviour = 1;
                        break;
                }
            }
        });
        mValueOverflowGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.value_over_error_checkbox:
                        program.valueOverflowBehaviour = 0;
                        break;
                    case R.id.value_over_wrap_checkbox:
                        program.valueOverflowBehaviour = 1;
                        break;
                    case R.id.value_over_cap_checkbox:
                        program.valueOverflowBehaviour = 2;
                        break;

                }
            }
        });
        mValueUnderflowGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.value_under_error_checkbox:
                        program.valueUnderflowBehaviour = 0;
                        break;
                    case R.id.value_under_wrap_checkbox:
                        program.valueUnderflowBehaviour = 1;
                        break;
                    case R.id.value_under_cap_checkbox:
                        program.valueUnderflowBehaviour = 2;
                        break;
                }
            }
        });

        program = getArguments().getParcelable("prog");
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
            mSizeInput.setText(Integer.toString(program.memSize));
            mMaxInput.setText(Integer.toString(program.maxValue));
            mMinInput.setText(Integer.toString(program.minValue));
            ((RadioButton) mPointerOverflowGroup.getChildAt(program.valueOverflowBehaviour)).setChecked(true);
            ((RadioButton) mPointerUnderflowGroup.getChildAt(program.valueUnderflowBehaviour)).setChecked(true);
            ((RadioButton) mValueOverflowGroup.getChildAt(program.pointerOverflowBehaviour)).setChecked(true);
            ((RadioButton) mValueUnderflowGroup.getChildAt(program.pointerUnderflowBehaviour)).setChecked(true);
        } else {
            program = new Program();
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
                int size = 10000;
                if(!mSizeInput.getText().toString().isEmpty()) {
                    size = Integer.parseInt(mSizeInput.getText().toString());
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
                }
                int defaultMax = (int)1E6;
                if(!mMaxInput.getText().toString().isEmpty()) {
                    defaultMax = Integer.parseInt(mMaxInput.getText().toString());
                }
                int defaultMin = 0;
                if(!mMinInput.getText().toString().isEmpty()) {
                    defaultMin = Integer.parseInt(mMinInput.getText().toString());
                }
                if(defaultMin > defaultMax) {
                    error = true;
                    mMaxWrapper.setError("Max value must larger than min");
                    mMinWrapper.setError("Min value must be less than max");
                } else {
                    mMaxWrapper.setError(null);
                    mMinWrapper.setError(null);
                }

                if(!error) {
                    program.name = mNameInput.getText().toString();
                    program.memSize = size;
                    program.maxValue = defaultMax;
                    program.minValue = defaultMin;
                    program.desc = mDescInput.getText().toString();
                    program.outputSuffix = mSuffixInput.getText().toString();
                    mListener.onPositiveClick(SettingsDialog.this, program);

                    SettingsDialog.this.getDialog().dismiss();
                }
            }
        });
        view.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onNegativeClick(SettingsDialog.this);
                SettingsDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }


    public interface SettingsDialogListener {

        void onNegativeClick(DialogFragment dialog);

        void onPositiveClick(DialogFragment dialog, Program program);

    }

}
