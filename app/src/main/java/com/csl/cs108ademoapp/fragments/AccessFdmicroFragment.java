package com.csl.cs108ademoapp.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.csl.cs108ademoapp.AccessTask;
import com.csl.cs108ademoapp.CustomPopupWindow;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.SelectTag;
import com.csl.cs108library4a.Cs108Connector;

public class AccessFdmicroFragment extends CommonFragment {
    SelectTag selectTag;
    Spinner spinnerSelectCommand, spinnerSelectAuth, spinnerSelectGetSource;
    TableRow tableRowOffsetLength, tableRowValue, tableRowAuth, tableRowGetTemperature, tableRowGetTemperature1, tableRowLogging, tableRowReg, tableRowEnable;
    EditText editTextMemoryValue;
    TextView textViewTemperatureValue, textViewBatteryValue, textViewLoggingValue, textViewLoggingValue1;
    Button buttonRead, buttonWrite;

    boolean operationRunning = false, operationRead = false, operationReadTemperature = false, operationReadBattery = false, operationSetLogging = false, operationCheckLogging = false, operationGetLogging = false;
    AccessTask accessTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, false);
        return inflater.inflate(R.layout.fragment_access_fdmicro, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        selectTag = new SelectTag((Activity) getActivity());

        tableRowOffsetLength = (TableRow) getActivity().findViewById(R.id.accessFDOffsetLengthRow);
        tableRowValue = (TableRow) getActivity().findViewById(R.id.accessFDValueRow);
        tableRowAuth = (TableRow) getActivity().findViewById(R.id.accessFDAuthRow);
        tableRowGetTemperature = (TableRow) getActivity().findViewById(R.id.accessFDGetTemperatureRow);
        tableRowGetTemperature1 = (TableRow) getActivity().findViewById(R.id.accessFDGetTemperatureRow1);
        tableRowLogging = (TableRow) getActivity().findViewById(R.id.accessFDLoggingRow);
        tableRowReg = (TableRow) getActivity().findViewById(R.id.accessFDRegRow);
        tableRowEnable = (TableRow) getActivity().findViewById(R.id.accessFDEnableRow);

        spinnerSelectCommand = (Spinner) getActivity().findViewById(R.id.selectCommand);
        ArrayAdapter<CharSequence> targetAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.fd_command_options, R.layout.custom_spinner_layout);
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectCommand.setAdapter(targetAdapter);
        spinnerSelectCommand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                commandSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextMemoryValue = (EditText) getActivity().findViewById(R.id.accessFDMemoryValue);

        spinnerSelectAuth = (Spinner) getActivity().findViewById(R.id.accessFDselectAuth);
        ArrayAdapter<CharSequence> targetAdapterAuth = ArrayAdapter.createFromResource(getActivity(), R.array.fd_auth_options, R.layout.custom_spinner_layout);
        targetAdapterAuth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectAuth.setAdapter(targetAdapterAuth);

        spinnerSelectGetSource = (Spinner) getActivity().findViewById(R.id.accessFDSelectGetSource);
        ArrayAdapter<CharSequence> targetAdapterGetSource = ArrayAdapter.createFromResource(getActivity(), R.array.fd_getSource_options, R.layout.custom_spinner_layout);
        targetAdapterAuth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectGetSource.setAdapter(targetAdapterGetSource);

        Button buttonCheckTemperature = (Button) getActivity().findViewById(R.id.accessFDcheckTemperature);
        buttonCheckTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOperationRunning()) return;
                operationReadTemperature = true;
                textViewTemperatureValue.setText("");
                readWriteOperation();
            }
        });
        textViewTemperatureValue = (TextView) getActivity().findViewById(R.id.accessFDtemperatureValue);

        Button buttonCheckBattery = (Button) getActivity().findViewById(R.id.accessFDcheckBattery);
        buttonCheckBattery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOperationRunning()) return;
                operationReadBattery = true;
                textViewBatteryValue.setText("");
                readWriteOperation();
            }
        });
        textViewBatteryValue = (TextView) getActivity().findViewById(R.id.accessFDbatteryValue);

        Button buttonSetLogging = (Button) getActivity().findViewById(R.id.accessFDSetLogging);
        buttonSetLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOperationRunning()) return;
                operationSetLogging = true;
                textViewLoggingValue.setText("");
                readWriteOperation();
            }
        });
        Button buttonCheckLogging = (Button) getActivity().findViewById(R.id.accessFDCheckLogging);
        buttonCheckLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOperationRunning()) return;
                operationCheckLogging = true;
                textViewLoggingValue.setText("");
                readWriteOperation();
            }
        });
        Button buttonGetLogging = (Button) getActivity().findViewById(R.id.accessFDGetLogging);
        buttonGetLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOperationRunning()) return;
                operationGetLogging = true;
                textViewLoggingValue1.setText("");
                readWriteOperation();
            }
        });
        textViewLoggingValue = (TextView) getActivity().findViewById(R.id.accessFDloggingValue);
        textViewLoggingValue1 = (TextView) getActivity().findViewById(R.id.accessFDloggingValue1);

        buttonRead = (Button) getActivity().findViewById(R.id.accessCCReadButton);
        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOperationRunning()) return;
                operationRead = true;
                readWriteOperation();
            }
        });

        buttonWrite = (Button) getActivity().findViewById(R.id.accessCCWriteButton);
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOperationRunning()) return;
                operationRead = false;
                readWriteOperation();
            }
        });

        MainActivity.mCs108Library4a.setSameCheck(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.mCs108Library4a.appendToLog("AccessFdmicro onResume !!!");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        MainActivity.mCs108Library4a.setSameCheck(true);
        MainActivity.mCs108Library4a.restoreAfterTagSelect();
        super.onDestroy();
    }

    boolean userVisibleHint = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            userVisibleHint = true;
            selectTag.updateBankSelected();
            MainActivity.mCs108Library4a.appendToLog("AccessFdmicroFragment is now VISIBLE");
        } else {
            userVisibleHint = false;
            MainActivity.mCs108Library4a.appendToLog("AccessFdmicroFragment is now INVISIBLE");
        }
    }

    public AccessFdmicroFragment() {
        super("AccessFdmicroFragment");
    }

    void commandSelected(int position) {
        if (position < 0 || position > 2) position = spinnerSelectCommand.getSelectedItemPosition();
        MainActivity.mCs108Library4a.appendToLog("commandSelected position = " + position);
        switch (position) {
            case 0:
                tableRowOffsetLength.setVisibility(View.VISIBLE);
                tableRowValue.setVisibility(View.VISIBLE);
                tableRowValue.setVisibility(View.VISIBLE);
                tableRowAuth.setVisibility(View.GONE);
                tableRowGetTemperature.setVisibility(View.GONE);
                tableRowGetTemperature1.setVisibility(View.GONE);
                tableRowLogging.setVisibility(View.GONE);
                tableRowReg.setVisibility(View.GONE);
                tableRowEnable.setVisibility(View.GONE);
                buttonRead.setText(getResources().getString(R.string.read_title));
                buttonWrite.setText("WRITE");
                buttonWrite.setVisibility(View.VISIBLE);
                break;
            case 1:
                tableRowOffsetLength.setVisibility(View.GONE);
                tableRowValue.setVisibility(View.GONE);
                tableRowAuth.setVisibility(View.VISIBLE);
                tableRowGetTemperature.setVisibility(View.GONE);
                tableRowGetTemperature1.setVisibility(View.GONE);
                tableRowLogging.setVisibility(View.GONE);
                tableRowReg.setVisibility(View.GONE);
                tableRowEnable.setVisibility(View.GONE);
                buttonRead.setText(getResources().getString(R.string.start_title));
                buttonWrite.setVisibility(View.GONE);
                break;
            case 2:
                tableRowOffsetLength.setVisibility(View.GONE);
                tableRowValue.setVisibility(View.GONE);
                tableRowAuth.setVisibility(View.GONE);
                tableRowGetTemperature.setVisibility(View.VISIBLE);
                tableRowGetTemperature1.setVisibility(View.VISIBLE);
                tableRowLogging.setVisibility(View.GONE);
                tableRowReg.setVisibility(View.GONE);
                tableRowEnable.setVisibility(View.GONE);
                buttonRead.setText(getResources().getString(R.string.read_title));
                buttonWrite.setText("MEASURE");
                buttonWrite.setVisibility(View.VISIBLE);
                break;
            case 3:
                tableRowOffsetLength.setVisibility(View.GONE);
                tableRowValue.setVisibility(View.GONE);
                tableRowAuth.setVisibility(View.GONE);
                tableRowGetTemperature.setVisibility(View.GONE);
                tableRowGetTemperature1.setVisibility(View.GONE);
                tableRowLogging.setVisibility(View.VISIBLE);
                tableRowReg.setVisibility(View.GONE);
                tableRowEnable.setVisibility(View.GONE);
                buttonRead.setText(getResources().getString(R.string.start_title));
                buttonWrite.setText("STOP");
                buttonWrite.setVisibility(View.VISIBLE);
                break;
            case 4:
                tableRowOffsetLength.setVisibility(View.GONE);
                tableRowValue.setVisibility(View.GONE);
                tableRowValue.setVisibility(View.GONE);
                tableRowAuth.setVisibility(View.GONE);
                tableRowGetTemperature.setVisibility(View.GONE);
                tableRowGetTemperature1.setVisibility(View.GONE);
                tableRowLogging.setVisibility(View.GONE);
                tableRowReg.setVisibility(View.VISIBLE);
                tableRowEnable.setVisibility(View.GONE);
                buttonRead.setText(getResources().getString(R.string.read_title));
                buttonWrite.setText("WRITE");
                buttonWrite.setVisibility(View.VISIBLE);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                tableRowOffsetLength.setVisibility(View.GONE);
                tableRowValue.setVisibility(View.GONE);
                tableRowValue.setVisibility(View.GONE);
                tableRowAuth.setVisibility(View.GONE);
                tableRowGetTemperature.setVisibility(View.GONE);
                tableRowGetTemperature1.setVisibility(View.GONE);
                tableRowLogging.setVisibility(View.GONE);
                tableRowReg.setVisibility(View.GONE);
                CheckBox checkBox = (CheckBox) getActivity().findViewById(R.id.accessFDEnable);
                if (position == 6) checkBox.setText("refresh temperature measurement");
                else checkBox.setText("Enable");
                if (position == 7) tableRowEnable.setVisibility(View.GONE);
                else tableRowEnable.setVisibility(View.VISIBLE);
                buttonRead.setText(getResources().getString(R.string.read_title));
                buttonRead.setText(getResources().getString(R.string.start_title));
                buttonWrite.setVisibility(View.GONE);
                break;
            default:
                buttonRead.setText(getResources().getString(R.string.read_title));
                buttonWrite.setText("WRITE");
                buttonWrite.setVisibility(View.VISIBLE);
                break;
        }
    }

    boolean isOperationRunning() {
        if (accessTask != null) {
            if (accessTask.getStatus() == AsyncTask.Status.RUNNING) {
                Toast.makeText(MainActivity.mContext, "Running acccess task. Please wait", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
    void readWriteOperation() {
        Cs108Connector.HostCommands hostCommand = null;
        if (isOperationRunning()) return;
        iOtherFlowCount = 0; bLogging = false; iTimeNumber = 0; iTimeTotal = 0;
        if (operationReadTemperature || operationReadBattery || operationSetLogging || operationCheckLogging || operationGetLogging) {
            operationRead = true;
            tableRowEnable.setVisibility(View.GONE);
            buttonRead.setText(getResources().getString(R.string.start_title));
            buttonWrite.setVisibility(View.GONE);

            MainActivity.mCs108Library4a.macWrite(0x117, 0x0000);
            hostCommand = Cs108Connector.HostCommands.CMD_FDM_OPMODE_CHECK;
        } else {
            MainActivity.mCs108Library4a.appendToLog("Item Selected is " + spinnerSelectCommand.getSelectedItemPosition());
            int position = spinnerSelectCommand.getSelectedItemPosition();
            switch (position) {
                case 0:
                    EditText editTextMemoryOffset = (EditText) getActivity().findViewById(R.id.accessFDmemoryOffset);
                    int iMemoryOffset = getEditTextHexValue(editTextMemoryOffset, 4);
                    iMemoryOffset &= 0xFFFC;
                    editTextMemoryOffset.setText(String.format("%X", iMemoryOffset));
                    MainActivity.mCs108Library4a.macWrite(0x11c, iMemoryOffset);

                    EditText editTextMemoryLength = (EditText) getActivity().findViewById(R.id.accessFDmemoryLength);
                    int iMemoryLength = 0;
                    try {
                        iMemoryLength = Integer.parseInt(editTextMemoryLength.getText().toString());
                    } catch (Exception ex) { }
                    if (iMemoryLength <= 0) iMemoryLength = 1;
                    if (operationRead) {
                        if (iMemoryLength != (iMemoryLength / 4) * 4)
                            iMemoryLength = ((iMemoryLength / 4) + 1) * 4;
                        if (iMemoryLength > 512) iMemoryLength = 512;
                    } else {
                        if (iMemoryLength > 4) iMemoryLength = 4;
                    }
                    editTextMemoryLength.setText(String.valueOf(iMemoryLength));
                    MainActivity.mCs108Library4a.macWrite(0x11d, iMemoryLength);

                    if (operationRead) editTextMemoryValue.setText("");
                    else {
                        String strValue = editTextMemoryValue.getText().toString();
                        if (strValue.length() > 8)
                            strValue = strValue.substring(strValue.length() - 8);
                        if (strValue.length() > 2 * iMemoryLength)
                            strValue = strValue.substring(strValue.length() - 2 * iMemoryLength);
                        int iValue = 0;
                        try {
                            iValue = Integer.parseInt(strValue, 16);
                        } catch (Exception ex) {
                            strValue = "00";
                        }
                        strValue = String.format("%X", iValue);
                        editTextMemoryValue.setText(strValue);
                        MainActivity.mCs108Library4a.macWrite(0x11e, iValue);
                    }

                    hostCommand = (operationRead ? Cs108Connector.HostCommands.CMD_FDM_RDMEM : Cs108Connector.HostCommands.CMD_FDM_WRMEM);
                    break;
                case 1:
                    int iConfig = spinnerSelectAuth.getSelectedItemPosition();
                    switch (iConfig) {
                        case 1:
                            iConfig = 3;
                            break;
                        case 2:
                            iConfig = 4;
                            break;
                        default:
                            break;
                    }
                    MainActivity.mCs108Library4a.macWrite(0x117, iConfig);    //0 (user area password), 3 (unlock password), 4 (stop logging password)

                    EditText editTextAuthPassword = (EditText) getActivity().findViewById(R.id.selectFDAuthPassword);
                    int iValue = getEditTextHexValue(editTextAuthPassword, 8);
                    MainActivity.mCs108Library4a.macWrite(0x11A, iValue);

                    hostCommand = Cs108Connector.HostCommands.CMD_FDM_AUTH;
                    break;
                case 2:
                    iConfig = 0;
                    CheckBox checkBoxGetTemperatureStartGet = (CheckBox) getActivity().findViewById(R.id.accessFDGetTemperatureStartGet);
                    if (true) {
                        if (operationRead) checkBoxGetTemperatureStartGet.setChecked(true);
                        else checkBoxGetTemperatureStartGet.setChecked(false);
                    }
                    if (checkBoxGetTemperatureStartGet.isChecked()) iConfig |= 0x80;
                    switch (spinnerSelectGetSource.getSelectedItemPosition()) {
                        case 1:
                            iConfig |= 0x10;
                            break;
                        case 2:
                            iConfig |= 0x20;
                            break;
                        case 3:
                            iConfig |= 0x30;
                            break;
                    }
                    CheckBox checkBoxGetTemperatureResultType = (CheckBox) getActivity().findViewById(R.id.accessFDGetTemperatureResultType);
                    if (checkBoxGetTemperatureResultType.isChecked()) iConfig |= 4;
                    CheckBox checkBoxGetTemperatureCheckField = (CheckBox) getActivity().findViewById(R.id.accessFDGetTemperatureCheckField);
                    if (checkBoxGetTemperatureCheckField.isChecked()) iConfig |= 2;
                    CheckBox checkBoxGetTemperatureStorageEnable = (CheckBox) getActivity().findViewById(R.id.accessFDGetTemperatureStorageEnable);
                    if (checkBoxGetTemperatureStorageEnable.isChecked()) iConfig |= 1;
                    MainActivity.mCs108Library4a.macWrite(0x117, iConfig);

                    EditText editTextStoreOffset = (EditText) getActivity().findViewById(R.id.accessFDStoreOffset);
                    int iStoreOffset = getEditTextHexValue(editTextStoreOffset, 2);
                    MainActivity.mCs108Library4a.macWrite(0x11b, iStoreOffset);

                    hostCommand = Cs108Connector.HostCommands.CMD_FDM_GET_TEMPERATURE;
                    break;
                case 3:
                    MainActivity.mCs108Library4a.macWrite(0x117, operationRead ? 0 : 80);

                    if (operationRead)
                        hostCommand = Cs108Connector.HostCommands.CMD_FDM_START_LOGGING;
                    else {
                        EditText editText = (EditText) getActivity().findViewById(R.id.selectFDLoggingPassword);
                        int iPassword = getEditTextHexValue(editText, 8);
                        MainActivity.mCs108Library4a.macWrite(0x11A, iPassword);

                        hostCommand = Cs108Connector.HostCommands.CMD_FDM_STOP_LOGGING;
                    }
                    break;
                case 4:
                    EditText editText = (EditText) getActivity().findViewById(R.id.accessFDRegOffset);
                    iValue = getEditTextHexValue(editText, 2);
                    iValue += 0xC000;
                    editText.setText(String.format("%04X", iValue));
                    MainActivity.mCs108Library4a.macWrite(0x118, iValue);

                    EditText editText1 = (EditText) getActivity().findViewById(R.id.accessFDRegValue);
                    if (operationRead) {
                        editText1.setText("");
                        hostCommand = Cs108Connector.HostCommands.CMD_FDM_RDREG;
                    } else {
                        int iValue1 = getEditTextHexValue(editText1, 4);
                        MainActivity.mCs108Library4a.macWrite(0x119, iValue1);
                        hostCommand = Cs108Connector.HostCommands.CMD_FDM_WRREG;
                    }
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                    CheckBox checkBox1 = (CheckBox) getActivity().findViewById(R.id.accessFDEnable);
                    iValue = 0;
                    if (checkBox1.isChecked()) {
                        if (position == 8) iValue = 2;
                        else if (position != 7) iValue = 1;
                    }
                    MainActivity.mCs108Library4a.macWrite(0x117, iValue);
                    if (position == 5) hostCommand = Cs108Connector.HostCommands.CMD_FDM_DEEP_SLEEP;
                    else if (position == 6)
                        hostCommand = Cs108Connector.HostCommands.CMD_FDM_OPMODE_CHECK;
                    else if (position == 7)
                        hostCommand = Cs108Connector.HostCommands.CMD_FDM_INIT_REGFILE;
                    else if (position == 8)
                        hostCommand = Cs108Connector.HostCommands.CMD_FDM_LED_CTRL;
                    break;
                default:
                    break;
            }
        }
        doAccessTask(hostCommand);
    }

    void doAccessTask(Cs108Connector.HostCommands hostCommand) {
        String selectMask = selectTag.editTextTagID.getText().toString();
        int selectBank = selectTag.spinnerSelectBank.getSelectedItemPosition() + 1;
        int selectOffset = Integer.valueOf(selectTag.editTextRWSelectOffset.getText().toString());
        boolean invalid = false;
        accessTask = new AccessTask(
                (operationRead ? buttonRead : buttonWrite), null,
                invalid,
                selectMask, selectBank, selectOffset,
                selectTag.editTextAccessRWAccPassword.getText().toString(),
                Integer.valueOf(selectTag.editTextaccessRWAntennaPower.getText().toString()),
                hostCommand,
                0, false, false, true,
                null, null, null, null, null);
        accessTask.setRunnable(updateRunnable);
        accessTask.execute();
    }

    Handler handler = new Handler(); boolean bLogging = false; int iOtherFlowCount = 0, iTimeNumber = 0, iTimeTotal = 0;
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (accessTask == null) return;
            MainActivity.mCs108Library4a.appendToLog("accessTask.status = " + accessTask.getStatus().toString());
            if (accessTask.getStatus() == AsyncTask.Status.RUNNING) {
                mHandler.postDelayed(updateRunnable, 100);
                return;
            }

            MainActivity.mCs108Library4a.appendToLog(accessTask.accessResult + ": blogging with iOtherFlowCount = " + iOtherFlowCount);
            if ((operationReadTemperature || operationReadBattery || operationSetLogging || operationCheckLogging || operationGetLogging) && (iOtherFlowCount == 0)) {
                int iValue = 0;
                try {
                    iValue = Integer.parseInt(accessTask.accessResult, 16);
                    MainActivity.mCs108Library4a.appendToLog(accessTask.accessResult + ": blogging with iValue = " + iValue);
                } catch (Exception ex) { }
                MainActivity.mCs108Library4a.appendToLog(accessTask.accessResult + ": blogging with iValue after catch = " + iValue);
                if ((iValue & 0x1000) != 0) {
                    MainActivity.mCs108Library4a.appendToLog(accessTask.accessResult + ": blogging A is true");
                    bLogging = true;
                    if (operationCheckLogging == false && operationGetLogging == false) {
                        iOtherFlowCount = 99;
                        accessTask.accessResult += ": logging is in processed";
                    }
                } else bLogging = false;
                if (operationReadBattery && (iValue & 0x0100) == 0) {
                    iOtherFlowCount = 99;
                    accessTask.accessResult += ": less than 0.9V or no battery.";
                }
            }
            if (operationGetLogging) {
                switch (iOtherFlowCount) {
                    case 0:
                        if (bLogging) {
                            MainActivity.mCs108Library4a.macWrite(0x118, 0xc094);
                            doAccessTask(Cs108Connector.HostCommands.CMD_FDM_RDREG);
                        } else {
                            MainActivity.mCs108Library4a.macWrite(0x011c, 0xb188);
                            MainActivity.mCs108Library4a.macWrite(0x11d, 4);
                            doAccessTask(Cs108Connector.HostCommands.CMD_FDM_RDMEM);
                        }
                        break;
                    case 1:
                        if (bLogging) {
                            operationGetLogging = false;
                            int iValue = 0;
                            try {
                                iValue = Integer.parseInt(accessTask.accessResult.substring(2,4), 16) & 0x30;
                            } catch (Exception ex) { }
                            String strMessage = "";
                            if (iValue == 0x10) strMessage = "wait to log";
                            else if (iValue == 0x20) strMessage = "logging";
                            else if (iValue == 00) strMessage = "non-rtc";
                            textViewLoggingValue1.setText(accessTask.accessResult + ": " + strMessage + "\n");
                        } else {
                            String strTemp = accessTask.accessResult;
                            iTimeTotal = Integer.parseInt(strTemp.substring(2, 4) + strTemp.substring(0, 2), 16);
                            textViewLoggingValue1.setText("status: " + accessTask.accessResult + "\n");
                        }
                    default:
                        if (iOtherFlowCount != 1) {
                            String strMessage = ""; //accessTask.accessResult + ": ";
                            String strAccessResult = accessTask.accessResult.substring(6, 8) + accessTask.accessResult.substring(4, 6) + accessTask.accessResult.substring(2, 4) + accessTask.accessResult.substring(0, 2);
                            Long lValue = Long.parseLong(strAccessResult, 16);
                            int iOddBit = 0;
                            MainActivity.mCs108Library4a.appendToLog(String.format("accessResult to lValue = %X", lValue));
                            for (int i = 0; i < 32; i++) {
                                if ((lValue & 1) != 0) {
                                    iOddBit++;
                                    MainActivity.mCs108Library4a.appendToLog("accessResult, i=" + i + ", iOddbit=" + iOddBit);
                                }
                                lValue = lValue >> 1;
                            }
                            if ((iOddBit & 1) != 0)
                                strMessage += ("invalid checksum, " + strAccessResult);
                            else {
                                iTimeNumber = Integer.parseInt(strAccessResult.substring(0, 4), 16) & 0x7FFF;
                                strMessage += ("item " + iTimeNumber + ", ");
                                int iTemperature = Integer.parseInt(strAccessResult.substring(5, 8), 16) & 0x3FF;
                                strMessage += i2TemperatureString(iTemperature);
                            }
                            textViewLoggingValue1.append(strMessage + "\n");
                        }

                        if (iTimeTotal < (iOtherFlowCount - 1) || bLogging) {
                            textViewLoggingValue1.append("end of logging data\n");
                            operationGetLogging = false;
                            commandSelected(spinnerSelectCommand.getSelectedItemPosition());
                        }
                        else {
                            MainActivity.mCs108Library4a.macWrite(0x011c, 0x1000 + (iOtherFlowCount - 2 + 1) * 4);
                            MainActivity.mCs108Library4a.macWrite(0x11d, 4);
                            doAccessTask(Cs108Connector.HostCommands.CMD_FDM_RDMEM);
                        }
                        break;
                }
                iOtherFlowCount++;
            } else if (operationCheckLogging) {
                switch (iOtherFlowCount) {
                    case 0:
                        MainActivity.mCs108Library4a.appendToLog(accessTask.accessResult + ": blogging B is " + bLogging);
                        if (bLogging) {
                            MainActivity.mCs108Library4a.macWrite(0x118, 0xc096);
                            doAccessTask(Cs108Connector.HostCommands.CMD_FDM_RDREG);
                        } else {
                            MainActivity.mCs108Library4a.macWrite(0x011c, 0xb188);
                            MainActivity.mCs108Library4a.macWrite(0x11d, 4);
                            doAccessTask(Cs108Connector.HostCommands.CMD_FDM_RDMEM);
                        }
                        break;
                    default:
                        operationCheckLogging = false;
                        textViewLoggingValue.setText(accessTask.accessResult);
                        commandSelected(spinnerSelectCommand.getSelectedItemPosition());
                        break;
                }
                iOtherFlowCount++;
            } else if (operationSetLogging) {
                MainActivity.mCs108Library4a.appendToLog("wallace.iOtherFlowCount = " + iOtherFlowCount + ", accessResult = " + accessTask.accessResult);
                switch (iOtherFlowCount) {
                    case 0:
                        MainActivity.mCs108Library4a.macWrite(0x11c, 0xb040);   //~user_cfg1,user_cfg1,~user_cfg0,user_cfg0: default as 0xd629b34c
                        MainActivity.mCs108Library4a.macWrite(0x11d, 4);
                        MainActivity.mCs108Library4a.macWrite(0x11e, 0x4cb329d6);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_WRMEM);
                        break;
                    case 1:
                        EditText editText = (EditText) getActivity().findViewById(R.id.accessFDrtcCntLimit);
                        int iValue = 3;
                        try {
                            iValue = Integer.parseInt(editText.getText().toString());
                        } catch (Exception ex) { }
                        editText.setText(String.valueOf(iValue));
                        String string1 = String.format("%04X", iValue);
                        MainActivity.mCs108Library4a.appendToLog("accessResult: string1 = " + string1);
                        String string2 = string1.substring(2,4) + string1.substring(0,2) + "0000";
                        MainActivity.mCs108Library4a.appendToLog("accessResult: string2 = " + string2);
                        iValue = Integer.parseInt(string2, 16);
                        MainActivity.mCs108Library4a.appendToLog(String.format("accessResult: iValue = %X", iValue));

                        MainActivity.mCs108Library4a.macWrite(0x11c, 0xb094);   //rtc_cnt_limit: default as 0x00000003
                        MainActivity.mCs108Library4a.macWrite(0x11d, 4);
                        MainActivity.mCs108Library4a.macWrite(0x11e, iValue);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_WRMEM);
                        break;
                    case 2:
                        editText = (EditText) getActivity().findViewById(R.id.accessFDstepCfg);
                        iValue = 3;
                        try {
                            iValue = Integer.parseInt(editText.getText().toString());
                        } catch (Exception ex) { }
                        editText.setText(String.valueOf(iValue));
                        string1 = String.format("%04X", iValue);
                        MainActivity.mCs108Library4a.appendToLog("accessResult: string1 = " + string1);
                        string2 = string1.substring(2,4) + string1.substring(0,2); string2 += string2;
                        MainActivity.mCs108Library4a.appendToLog("accessResult: string2 = " + string2);
                        iValue = Integer.parseInt(string2, 16);
                        MainActivity.mCs108Library4a.appendToLog(String.format("accessResult: iValue = %X", iValue));

                        MainActivity.mCs108Library4a.macWrite(0x11c, 0xb0a4);   //vdet_alarm_step_cfg, vdet_step_cfg: default as 0
                        MainActivity.mCs108Library4a.macWrite(0x11d, 4);
                        MainActivity.mCs108Library4a.macWrite(0x11e, 0x0A000100);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_WRMEM);
                        break;
                    case 3:
                        editText = (EditText) getActivity().findViewById(R.id.accessFDvdetDelayStartCfg);
                        iValue = 1;
                        try {
                            iValue = Integer.parseInt(editText.getText().toString());
                        } catch (Exception ex) { }
                        editText.setText(String.valueOf(iValue));
                        MainActivity.mCs108Library4a.appendToLog(String.format("accessResult: iValue = %X", iValue));

                        MainActivity.mCs108Library4a.macWrite(0x118, 0xc084);   //vdet_delay_cfg: default as 0xffff in minute
                        MainActivity.mCs108Library4a.macWrite(0x119, iValue);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_WRREG);
                        break;
                    case 4:
                        editText = (EditText) getActivity().findViewById(R.id.accessFDstepCfg);
                        iValue = 1;
                        try {
                            iValue = Integer.parseInt(editText.getText().toString());
                        } catch (Exception ex) { }
                        editText.setText(String.valueOf(iValue));
                        MainActivity.mCs108Library4a.appendToLog(String.format("accessResult: iValue = %X", iValue));

                        MainActivity.mCs108Library4a.macWrite(0x118, 0xc085);   //vdet_step_cfg: default as 0xffff in seconds
                        MainActivity.mCs108Library4a.macWrite(0x119, iValue);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_WRREG);
                        break;
                    case 5:
                        MainActivity.mCs108Library4a.macWrite(0x118, 0xc099);   //summary_min_temperature: default 0
                        MainActivity.mCs108Library4a.macWrite(0x119, 0);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_WRREG);
                        break;
                    case 6:
                        MainActivity.mCs108Library4a.macWrite(0x118, 0xc098);   //summary_max_temperature: default 0
                        MainActivity.mCs108Library4a.macWrite(0x119, 0x100);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_WRREG);
                        break;
                    case 7:
                        MainActivity.mCs108Library4a.macWrite(0x117, 0);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_START_LOGGING);
                        break;
                    case 8:
                        MainActivity.mCs108Library4a.macWrite(0x118, 0xc084);   //vdet_delay_cfg: default as 0xffff in minute
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_RDREG);
                        break;
                    default:
                        operationSetLogging = false;
                        if (iOtherFlowCount < 88) {
                            String strMessage = accessTask.accessResult;
                            iValue = Integer.parseInt(accessTask.accessResult, 16);
                            if (iValue != 0xFFFF) strMessage += ": Okay\n";
                            else strMessage += ": invalid logging. Please stop it and try again.\n";
                            textViewLoggingValue.setText(strMessage);
                        } else textViewLoggingValue.setText(accessTask.accessResult);
                        commandSelected(spinnerSelectCommand.getSelectedItemPosition());
                        break;
                }
                iOtherFlowCount++;
            }
            else if (operationReadBattery) {
                switch(iOtherFlowCount) {
                    case 0:
                        MainActivity.mCs108Library4a.macWrite(0x119, 0x0008);
                        MainActivity.mCs108Library4a.macWrite(0x118, 0xc012);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_WRREG);
                        break;
                    case 1:
                        MainActivity.mCs108Library4a.macWrite(0x117, 0x12);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_GET_TEMPERATURE);
                        break;
                    case 2:
                        MainActivity.mCs108Library4a.macWrite(0x117, 0x92);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_GET_TEMPERATURE);
                        break;
                    default:
                        operationReadBattery = false;
                        if (iOtherFlowCount < 88) {
                            int iValue = 0;
                            try {
                                iValue = Integer.parseInt(accessTask.accessResult, 16);
                                float fValue = (float) (iValue & 0xFFFF);
                                fValue = fValue / 8192 * (float) 2.5;
                                textViewBatteryValue.setText(String.format("%.2fV", fValue));
                            } catch (Exception ex) {
                            }
                        } else textViewBatteryValue.setText(accessTask.accessResult);
                        commandSelected(spinnerSelectCommand.getSelectedItemPosition());
                        break;
                }
                iOtherFlowCount++;
            }
            else if (operationReadTemperature) {
                switch(iOtherFlowCount) {
                    case 0:
                        MainActivity.mCs108Library4a.macWrite(0x119, 0x0000);
                        MainActivity.mCs108Library4a.macWrite(0x118, 0xc012);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_WRREG);
                        break;
                    case 1:
                        MainActivity.mCs108Library4a.macWrite(0x117, 0x06);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_GET_TEMPERATURE);
                        break;
                    case 2:
                        MainActivity.mCs108Library4a.macWrite(0x117, 0x86);
                        doAccessTask(Cs108Connector.HostCommands.CMD_FDM_GET_TEMPERATURE);
                        break;
                    default:
                        operationReadTemperature = false;
                        if (iOtherFlowCount < 88) {
                            int iValue = 0;
                            try {
                                iValue = Integer.parseInt(accessTask.accessResult, 16);
                                if ((iValue & 0x8000) != 0)
                                    textViewTemperatureValue.setText("Store addr overflow");
                                else textViewTemperatureValue.setText(i2TemperatureString(iValue));
                            } catch (Exception ex) {
                            }
                        } else textViewTemperatureValue.setText(accessTask.accessResult);
                        commandSelected(spinnerSelectCommand.getSelectedItemPosition());
                        break;
                }
                iOtherFlowCount++;
            } else {
                int position = spinnerSelectCommand.getSelectedItemPosition();
                switch (position) {
                    case 0:
                        if (operationRead) editTextMemoryValue.setText(accessTask.accessResult);
                        break;
                    case 1:
                        int iValue = Integer.parseInt(accessTask.accessResult, 16);
                        String strMessage = accessTask.accessResult + ":";
                        switch (iValue & 0x7) {
                            case 0:
                                strMessage = "User area";
                                break;
                            case 3:
                                strMessage = "Unlock";
                                break;
                            case 4:
                                strMessage = "Stop loggging";
                                break;
                        }
                        strMessage += " password:\nAuth is ";
                        if ((iValue & 0x80) != 0) strMessage += "passed";
                        else strMessage += "failed";
                        strMessage += "\nPassword is ";
                        if ((iValue & 0x40) != 0) strMessage += "zero";
                        else strMessage += "non-zero";
                        CustomPopupWindow customPopupWindow = new CustomPopupWindow(MainActivity.mContext);
                        customPopupWindow.popupStart(strMessage, false);
                        break;
                    case 2:
                        MainActivity.mCs108Library4a.appendToLog("accessResult = " + accessTask.accessResult);
                        strMessage = accessTask.accessResult;
                        CheckBox checkBoxGetTemperatureResultType = (CheckBox) getActivity().findViewById(R.id.accessFDGetTemperatureResultType);
                        if (operationRead) {
                            iValue = Integer.parseInt(strMessage, 16);
                            if ((iValue & 0x8000) != 0) strMessage += ": store addr overflow";
                            else if (checkBoxGetTemperatureResultType.isChecked()) {
                                strMessage += ": temperature = ";
                                strMessage += i2TemperatureString(iValue);
                            } else
                                strMessage += String.format(": raw temperature data = %X", iValue & 0x1FFF);
                        } else {
                            if (strMessage.matches("FFFA")) strMessage += ": enough field energy";
                            else if (strMessage.matches("FFF5"))
                                strMessage += ": insufficient field energy";
                            else if (strMessage.matches("FFF0"))
                                strMessage += ": not yet enable field check";
                        }
                        customPopupWindow = new CustomPopupWindow(MainActivity.mContext);
                        customPopupWindow.popupStart(strMessage, false);
                        break;
                    case 3:
                        editTextMemoryValue.setText(accessTask.accessResult);
                        strMessage = accessTask.accessResult.trim();
                        if (operationRead) {
                            if (strMessage.matches("0000")) strMessage = null;
                        } else {
                            iValue = Integer.parseInt(strMessage, 16);
                            if ((iValue & 2) != 0) strMessage += ": password check is failed";
                            if ((iValue & 1) != 0) strMessage += ": RTC stop password is all zero";
                        }
                        if (strMessage != null) {
                            customPopupWindow = new CustomPopupWindow(MainActivity.mContext);
                            customPopupWindow.popupStart(strMessage, false);
                        }
                        break;
                    case 4:
                        strMessage = null;
                        if (operationRead) {
                            if (false && accessTask.accessResult.matches("FFFF"))
                                strMessage = accessTask.accessResult + ": invalid empty address";
                            else {
                                EditText editText = (EditText) getActivity().findViewById(R.id.accessFDRegValue);
                                editText.setText(accessTask.accessResult);
                            }
                        } else {
                            iValue = 0;
                            try {
                                iValue = Integer.parseInt(accessTask.accessResult, 16);
                            } catch (Exception ex) {
                            }
                            if (iValue != 0) {
                                strMessage = accessTask.accessResult;
                                if (iValue == (byte) 0xFFFF) strMessage += ": invalid/busy address";
                                else {
                                    if ((iValue & 0x4) != 0)
                                        strMessage += ": the register cannot be written";
                                    if ((iValue & 0x02) != 0)
                                        strMessage += ": invalid register addresss";
                                }
                            }
                        }
                        if (strMessage != null) {
                            customPopupWindow = new CustomPopupWindow(MainActivity.mContext);
                            customPopupWindow.popupStart(strMessage, false);
                        }
                        break;
                    case 5:
                    case 7:
                    case 8:
                        strMessage = accessTask.accessResult.trim();
                        if (strMessage != null && strMessage.length() != 0 && strMessage.matches("0000") == false) {
                            customPopupWindow = new CustomPopupWindow(MainActivity.mContext);
                            customPopupWindow.popupStart(strMessage, false);
                        }
                        break;
                    case 6:
                        strMessage = accessTask.accessResult;
                        iValue = Integer.parseInt(strMessage, 16);
                        if ((iValue & 0x2000) != 0) strMessage += ": user_access_en";
                        if ((iValue & 0x1000) != 0) strMessage += ": rtc logging";
                        if ((iValue & 0x800) != 0) strMessage += ": vdet_process_flag";
                        if ((iValue & 0x200) != 0) strMessage += ": light_chk_flag";
                        if ((iValue & 0x100) != 0) strMessage += ": vbat_pwr_flag";
                        customPopupWindow = new CustomPopupWindow(MainActivity.mContext);
                        customPopupWindow.popupStart(strMessage, false);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    String i2TemperatureString(int iValue) {
        String strMessage = "";
        if ((iValue & 0x200) != 0) {
            strMessage += "-";
            iValue ^= 0x3FF;
            iValue++;
        }
        if (true) { //8 bit data, 2 bit decimal
            strMessage += String.format("%d.", (iValue >> 2) & 0xFF);
            strMessage += String.format("%d\u2103", (iValue & 3) * 100 / 4);
        } else {    //7 bit data, 3 bit decimal
            strMessage += String.format("%d.", (iValue >> 3) & 0x7F);
            strMessage += String.format("%d\u2103", (iValue & 7) * 1000 / 8);
        }
        return strMessage;
    }

    int getEditTextHexValue(EditText editText, int iStrLen) {
        String strValue = editText.getText().toString().trim();
        MainActivity.mCs108Library4a.appendToLog("getEditTextHexValue: editText.string=" + strValue + ", iStrLen=" + iStrLen);
        if (strValue.length() > iStrLen) strValue = strValue.substring(strValue.length() - iStrLen);
        int iValue = 0;
        try {
            iValue = Integer.parseInt(strValue, 16);
        } catch (Exception ex) { }
        int iValue2 = 0;
        for (int i = 0; i < iStrLen; i++) { iValue2 <<= 4; iValue2 |= 0xFF; }
        strValue = String.format("%0" + String.valueOf(iStrLen) + "X", iValue & iValue2);
        MainActivity.mCs108Library4a.appendToLog("getEditTextHexValue: exit string = " + strValue);
        editText.setText(strValue);
        return iValue;
    }
}
