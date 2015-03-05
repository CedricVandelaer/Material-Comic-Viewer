package com.comicviewer.cedric.comicviewer.PreferenceFiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;

public class CustomMultiSelectListPreference extends ListPreference {

    private String separator;
    private static final String DEFAULT_SEPARATOR = "\u0001\u0007\u001D\u0007\u0001";
    private boolean[] entryChecked;
    private String dialogTitle;

    public CustomMultiSelectListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        entryChecked = new boolean[getEntries().length];
        separator = DEFAULT_SEPARATOR;
    }

    public CustomMultiSelectListPreference(Context context, ArrayList<CharSequence> keys, ArrayList<CharSequence> values) {
        super(context, null);
        
        CharSequence[] charSqKeys = new CharSequence[keys.size()];
        CharSequence[] charSqValues = new CharSequence[values.size()];
        
        dialogTitle=null;
        
        for (int i=0;i<keys.size();i++)
        {
            charSqKeys[i] = keys.get(i);
            charSqValues[i] = values.get(i);
        }
        
        setEntries(charSqKeys);
        setEntryValues(charSqValues);
        entryChecked = new boolean[getEntries().length];
        separator = DEFAULT_SEPARATOR;
    }

    public CustomMultiSelectListPreference(Context context, ArrayList<CharSequence> keys, ArrayList<CharSequence> values, String dialogtitle) {
        super(context, null);

        CharSequence[] charSqKeys = new CharSequence[keys.size()];
        CharSequence[] charSqValues = new CharSequence[values.size()];

        for (int i=0;i<keys.size();i++)
        {
            charSqKeys[i] = keys.get(i);
            charSqValues[i] = values.get(i);
        }
        
        dialogTitle = dialogtitle;

        setEntries(charSqKeys);
        setEntryValues(charSqValues);
        entryChecked = new boolean[getEntries().length];
        separator = DEFAULT_SEPARATOR;
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        
        if (dialogTitle!=null)
            builder.setTitle(dialogTitle);
        
        if (entries == null || entryValues == null
                || entries.length != entryValues.length) {
            throw new IllegalStateException(
                    "MultiSelectListPreference requires an entries array and an entryValues "
                            + "array which are both the same length");
        }

        restoreCheckedEntries();


        String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";
        
        OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean val) {
                entryChecked[which] = val;
            }
        };
        builder.setMultiChoiceItems(entries, entryChecked, listener);


        
    }

    private CharSequence[] unpack(CharSequence val) {
        if (val == null || "".equals(val)) {
            return new CharSequence[0];
        } else {
            return ((String) val).split(separator);
        }
    }

    /**
     * Gets the entries values that are selected
     *
     * @return the selected entries values
     */
    public CharSequence[] getCheckedValues() {
        return unpack(getValue());
    }

    private void restoreCheckedEntries() {
        CharSequence[] entryValues = getEntryValues();

        // Explode the string read in sharedpreferences
        CharSequence[] vals = unpack(getValue());

        if (vals != null) {
            List<CharSequence> valuesList = Arrays.asList(vals);
            for (int i = 0; i < entryValues.length; i++) {
                CharSequence entry = entryValues[i];
                entryChecked[i] = valuesList.contains(entry);
            }
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        List<CharSequence> selectedValues = new ArrayList<CharSequence>();

        CharSequence[] entryValues = getEntryValues();
        if (positiveResult && entryValues != null) {
            for (int i = 0; i < entryValues.length; i++) {
                if (entryChecked[i] == true) {
                    String val = (String) entryValues[i];
                    selectedValues.add(val);
                }
            }

            String value = join(selectedValues, separator);
            //setSummary(prepareSummary(values));
            setValueAndEvent(value);
        }
        
        /*Code to remove the selected values from the preferences*/
        /////////////////////////////////////////////////////
        if (positiveResult && entryValues!=null)
        {
            List<CharSequence> newItemList = new ArrayList<CharSequence>();
            for (int i=0;i<entryValues.length;i++)
            {
                CharSequence entry = entryValues[i];
                boolean isRemoved=false;
                for (int j=0;j<selectedValues.size();j++)
                {
                    if (entry.equals(selectedValues.get(j)))
                    {
                        isRemoved=true;
                    }
                }
                if (!isRemoved)
                    newItemList.add(entry);
            }
            

            StringBuilder csvList = new StringBuilder();
            String defaultPath = Environment.getExternalStorageDirectory().toString() + "/ComicViewer";
            if (newItemList.size()<1)
                newItemList.add(defaultPath);
            for(int i=0;i<newItemList.size();i++){
                csvList.append(newItemList.get(i));
                csvList.append(",");
            }
            Log.d("CustomMulti", "OnDialogClosedCalled, new Paths: "+csvList);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor sharedPreferencesEditor = prefs.edit();
            sharedPreferencesEditor.putString("Filepaths", csvList.toString());
            
            CharSequence[] charSqNewList = new CharSequence[newItemList.size()];
            setEntries(newItemList.toArray(charSqNewList));
            setEntryValues(charSqNewList);

            sharedPreferencesEditor.apply();

            //add to excluded paths
            sharedPreferencesEditor = prefs.edit();
            csvList = new StringBuilder();
            for(int i=0;i<selectedValues.size();i++){
                csvList.append(selectedValues.get(i));
                csvList.append(",");
            }
            sharedPreferencesEditor.putString("Excludedpaths", csvList.toString());
            sharedPreferencesEditor.apply();
            
        }
        /////////////////////////////////////////////////////
    }
    

    private void setValueAndEvent(String value) {
        if (callChangeListener(unpack(value))) {
            setValue(value);
        }
    }

    /*
    private CharSequence prepareSummary(List<CharSequence> joined) {
        List<String> titles = new ArrayList<String>();
        CharSequence[] entryTitle = getEntries();
        CharSequence[] entryValues = getEntryValues();
        int ix = 0;
        for (CharSequence value : entryValues) {
            if (joined.contains(value)) {
                titles.add((String) entryTitle[ix]);
            }
            ix += 1;
        }
        return join(titles, ", ");
    }
    */

    @Override
    protected Object onGetDefaultValue(TypedArray typedArray, int index) {
        return typedArray.getTextArray(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue,
                                     Object rawDefaultValue) {
        String value = null;
        CharSequence[] defaultValue;
        if (rawDefaultValue == null) {
            defaultValue = new CharSequence[0];
        } else {
            defaultValue = (CharSequence[]) rawDefaultValue;
        }
        List<CharSequence> joined = Arrays.asList(defaultValue);
        String joinedDefaultValue = join(joined, separator);
        if (restoreValue) {
            value = getPersistedString(joinedDefaultValue);
        } else {
            value = joinedDefaultValue;
        }

        //setSummary(prepareSummary(Arrays.asList(unpack(value))));
        setValueAndEvent(value);
    }

    /**
     * Joins array of object to single string by separator
     *
     * Credits to kurellajunior on this post
     * http://snippets.dzone.com/posts/show/91
     *
     * @param iterable
     *            any kind of iterable ex.: <code>["a", "b", "c"]</code>
     * @param separator
     *            separetes entries ex.: <code>","</code>
     * @return joined string ex.: <code>"a,b,c"</code>
     */
    protected static String join(Iterable<?> iterable, String separator) {
        Iterator<?> oIter;
        if (iterable == null || (!(oIter = iterable.iterator()).hasNext()))
            return "";
        StringBuilder oBuilder = new StringBuilder(String.valueOf(oIter.next()));
        while (oIter.hasNext())
            oBuilder.append(separator).append(oIter.next());
        return oBuilder.toString();
    }

}
