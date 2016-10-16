package com.tpb.brainfuck_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by theo on 23/08/16.
 */
public class SettingsDialog extends DialogFragment {
    private static final String TAG = "SettingsDialog";
    private static SettingsDialogListener mListener;
    private Program mProgram;
    private SettingsLaunchType mLaunchType;

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
        final TextView mTitle = (TextView) view.findViewById(R.id.settings_title);
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
        final Button mOkButton = (Button) view.findViewById(R.id.button_ok);
        final Button mCancelBUtton = (Button) view.findViewById(R.id.button_cancel);

        mLaunchType = (SettingsLaunchType) getArguments().getSerializable("launchType");
        if(mLaunchType == SettingsLaunchType.RUN) {
            mTitle.setText(R.string.title_settings_run_dialog);
            mOkButton.setText(R.string.title_settings_run_dialog);
        } else {
            mTitle.setText(R.string.title_settings_save_dialog);
            mOkButton.setText(R.string.title_settings_save_dialog);
        }

        mProgram = getArguments().getParcelable("prog");
        if(mProgram != null) {
            if(mProgram.name != null && !mProgram.name.isEmpty()) {
                mNameInput.setText(mProgram.name);
            }
            if(mProgram.desc != null && !mProgram.desc.isEmpty()) {
                mDescInput.setText(mProgram.desc);
            }
            if(!mProgram.outputSuffix.isEmpty()) {
                mSuffixInput.setText(mProgram.outputSuffix);
            }
            mSizeInput.setText(Integer.toString(mProgram.memSize));
            mMaxInput.setText(Integer.toString(mProgram.maxValue));
            mMinInput.setText(Integer.toString(mProgram.minValue));
            ((RadioButton) mPointerOverflowGroup.getChildAt(mProgram.valueOverflowBehaviour)).setChecked(true);
            ((RadioButton) mPointerUnderflowGroup.getChildAt(mProgram.valueUnderflowBehaviour)).setChecked(true);
            ((RadioButton) mValueOverflowGroup.getChildAt(mProgram.pointerOverflowBehaviour)).setChecked(true);
            ((RadioButton) mValueUnderflowGroup.getChildAt(mProgram.pointerUnderflowBehaviour)).setChecked(true);
        } else {
            mProgram = new Program();
        }

        builder.setView(view);

        mPointerOverflowGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.pointer_over_error_checkbox:
                        mProgram.pointerOverflowBehaviour = 0;
                        break;
                    case R.id.pointer_over_wrap_checkbox:
                        mProgram.pointerOverflowBehaviour = 1;
                        break;
                    case R.id.pointer_over_expand_checkbox:
                        mProgram.pointerOverflowBehaviour = 2;
                        break;
                }
            }
        });
        mPointerUnderflowGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.pointer_under_error_checkbox:
                        mProgram.pointerUnderflowBehaviour = 0;
                        break;
                    case R.id.pointer_under_wrap_checkbox:
                        mProgram.pointerUnderflowBehaviour = 1;
                        break;
                }
            }
        });
        mValueOverflowGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.value_over_error_checkbox:
                        mProgram.valueOverflowBehaviour = 0;
                        break;
                    case R.id.value_over_wrap_checkbox:
                        mProgram.valueOverflowBehaviour = 1;
                        break;
                    case R.id.value_over_cap_checkbox:
                        mProgram.valueOverflowBehaviour = 2;
                        break;

                }
            }
        });
        mValueUnderflowGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.value_under_error_checkbox:
                        mProgram.valueUnderflowBehaviour = 0;
                        break;
                    case R.id.value_under_wrap_checkbox:
                        mProgram.valueUnderflowBehaviour = 1;
                        break;
                    case R.id.value_under_cap_checkbox:
                        mProgram.valueUnderflowBehaviour = 2;
                        break;
                }
            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;
                if(mNameInput.getText().toString().isEmpty()) {
                    error = true;
                    mNameWrapper.setError(getResources().getString(R.string.error_no_program_name));
                } else {
                    mNameInput.setError(null);
                }
                int size = 10000;
                if(!mSizeInput.getText().toString().isEmpty()) {
                    size = Integer.parseInt(mSizeInput.getText().toString());
                    if(size < 1) {
                        error = true;
                        mSizeInput.setError(getResources().getString(R.string.error_min_memory_size));
                    } else {
                        mSizeInput.setError(null);
                    }
                    if(size > 100000) {
                        error = true;
                        mSizeInput.setError(getResources().getString(R.string.error_max_memory_size));
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
                    mMaxWrapper.setError(getResources().getString(R.string.error_max_min));
                    mMinWrapper.setError(getResources().getString(R.string.error_min_max));
                } else {
                    mMaxWrapper.setError(null);
                    mMinWrapper.setError(null);
                }
                if(!error) {
                    mProgram.name = mNameInput.getText().toString();
                    mProgram.memSize = size;
                    mProgram.maxValue = defaultMax;
                    mProgram.minValue = defaultMin;
                    mProgram.desc = mDescInput.getText().toString();
                    mProgram.outputSuffix = mSuffixInput.getText().toString();
                    mListener.onPositiveClick(SettingsDialog.this, mLaunchType, mProgram);

                    SettingsDialog.this.getDialog().dismiss();
                }
            }
        });
        mCancelBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onNegativeClick(SettingsDialog.this, mLaunchType);
                SettingsDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }


    public interface SettingsDialogListener {

        void onNegativeClick(DialogFragment dialog, SettingsLaunchType launchType);

        void onPositiveClick(DialogFragment dialog, SettingsLaunchType launchType, Program program);

    }

    public enum SettingsLaunchType {
        SAVE, RUN, CLOSE
    }

}
