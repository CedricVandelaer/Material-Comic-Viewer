package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.Model.Comic;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.StorageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Cédric on 2/08/2015.
 */
public class CollectionDialogHelper {

    private CharSequence[] smartCollectionType = {"Series",
            "Year",
            "Folders",
            "Writer",
            "Penciller",
            "Inker",
            "Colorist",
            "Letterer",
            "Editor",
            "Cover artist",
            "Story arc",
            "Character"
    };

    private Context mContext;

    public CollectionDialogHelper(Context context)
    {
        mContext = context;
    }

    public void showCollectionNameDialog(final RecyclerView.Adapter adapter)
    {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title("Add new collection")
                .input("Name", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        materialDialog.dismiss();
                        StorageManager.createCollection(mContext, charSequence.toString());
                        if (adapter!=null)
                            adapter.notifyDataSetChanged();
                        showSelectFilterTypeDialog(charSequence.toString());
                    }
                })
                .positiveText(mContext.getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(mContext))
                .negativeText(mContext.getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(mContext))
                .show();
    }

    protected void showSelectFilterTypeDialog(final String collectionName)
    {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title("Add filters")
                .items(smartCollectionType)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        /*
                        {"Series",
                            "Year",
                            "Folders",
                            "Writer",
                            "Penciller",
                            "Inker",
                            "Colorist",
                            "Letterer",
                            "Editor",
                            "Cover artist",
                            "Story arc",
                            "Character"
                         };
                         */
                        switch (i) {
                            case 0:
                                showSeriesListDialog(collectionName);
                                break;
                            case 1:
                                showYearListDialog(collectionName);
                                break;
                            case 2:
                                showFolderListDialog(collectionName);
                                break;
                            case 3:
                                showWriterListDialog(collectionName);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .positiveText(mContext.getResources().getString(R.string.finish))
                .positiveColor(StorageManager.getAppThemeColor(mContext))
                .show();
    }

    private void showWriterListDialog(String collectionName) {
        //TODO: rewrite dialog with add new button and multicallbacks
    }

    protected void showAddFilterDialogMultiOption(final String collectionName, String title, CharSequence[] options, MaterialDialog.ListCallbackMultiChoice callback)
    {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(title)
                .items(options)
                .itemsCallbackMultiChoice(null, callback)
                .negativeText(mContext.getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(mContext))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showSelectFilterTypeDialog(collectionName);
                    }
                })
                .positiveText(mContext.getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(mContext))
                .show();
    }

    protected void showAddFilterDialogSingleOption(final String collectionName, String title, CharSequence[] options, MaterialDialog.ListCallback callback)
    {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(title)
                .items(options)
                .itemsCallback(callback)
                .negativeText(mContext.getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(mContext))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showSelectFilterTypeDialog(collectionName);
                    }
                })
                .show();
    }

    protected void showSeriesListDialog(final String collectionName)
    {
        final CharSequence[] series = getCurrentSeries(true);

        showAddFilterDialogSingleOption(collectionName,
                "Select series",
                series,
                new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                        if (i == series.length-1)
                        {
                            showAskNameDialog(collectionName, "Add series", "title",
                                    new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                            CollectionActions.addSeriesFilterToCollection(mContext, collectionName, charSequence.toString());
                                            showSelectFilterTypeDialog(collectionName);
                                        }
                                    }
                            );
                        }
                        else
                        {
                            CollectionActions.addSeriesFilterToCollection(mContext, collectionName, charSequence.toString());
                            showSelectFilterTypeDialog(collectionName);
                        }
                    }
                });
    }

    protected void showAskNameDialog(final String collectionName, String title, String hint, MaterialDialog.InputCallback inputCallback)
    {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(title)
                .negativeText(mContext.getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(mContext))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showSelectFilterTypeDialog(collectionName);
                    }
                })
                .input(hint, "", false, inputCallback)
                .positiveText(mContext.getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(mContext))
                .show();
    }

    protected void showYearListDialog(final String collectionName)
    {
        final CharSequence[] years = getCurrentYears(true);

        showAddFilterDialogSingleOption(collectionName,
                "Select year",
                years,
                new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (i == years.length)
                        {
                            showAskNameDialog(collectionName, "Add year", "Year", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                    try
                                    {
                                        Integer.parseInt(charSequence.toString());
                                        CollectionActions.addYearsFilterToCollection(mContext, collectionName, charSequence.toString());
                                        showSelectFilterTypeDialog(collectionName);
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                        Toast.makeText(mContext, "Invalid year", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            CollectionActions.addYearsFilterToCollection(mContext, collectionName, charSequence.toString());
                            showSelectFilterTypeDialog(collectionName);
                        }
                    }
                });
    }

    protected void showFolderListDialog(final String collectionName)
    {
        showAddFilterDialogMultiOption(collectionName,
                "Select folders",
                getCurrentFolders(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        ArrayList<String> folders = new ArrayList<String>();
                        for (int i = 0; i < charSequences.length; i++)
                            folders.add(charSequences[i].toString());
                        CollectionActions.batchAddFolderFilterToCollection(mContext, collectionName, folders);
                        showSelectFilterTypeDialog(collectionName);
                        return false;
                    }
                });
    }

    protected CharSequence[] getCurrentFolders()
    {
        List<Comic> comics = ComicActions.getAllSimpleComics(mContext);
        Set<String> filepathSet = new HashSet<>();

        for (Comic comic:comics) {
            if (!comic.getFilePath().trim().equals(""))
                filepathSet.add(""+comic.getFilePath());
        }

        ArrayList<String> folders = Utilities.getStringsFromSet(filepathSet);
        Collections.sort(folders);
        CharSequence[] folderCharSequences = new CharSequence[folders.size()];

        for (int i=0;i<folders.size();i++)
            folderCharSequences[i] = folders.get(i);

        return folderCharSequences;
    }

    protected CharSequence[] getCurrentSeries(boolean includeAddSeriesOption)
    {
        List<Comic> comics = ComicActions.getAllSimpleComics(mContext);
        Set<String> seriesSet = new HashSet<>();

        for (Comic comic:comics) {
            if (!comic.getTitle().trim().equals(""))
                seriesSet.add(comic.getTitle());
        }

        ArrayList<String> series = Utilities.getStringsFromSet(seriesSet);
        Collections.sort(series);
        CharSequence[] seriesCharSequences;
        if (includeAddSeriesOption) {
            seriesCharSequences = new CharSequence[series.size() + 1];
            seriesCharSequences[series.size()] = "Add new series";
        }
        else
            seriesCharSequences = new CharSequence[series.size()];

        for (int i=0;i<series.size();i++)
            seriesCharSequences[i] = series.get(i);


        return seriesCharSequences;
    }

    protected CharSequence[] getCurrentYears(boolean includeAddYearsOption)
    {
        List<Comic> comics = ComicActions.getAllSimpleComics(mContext);
        Set<String> yearsSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getYear()!=-1)
                yearsSet.add(""+comic.getYear());
        }

        ArrayList<String> years = Utilities.getStringsFromSet(yearsSet);
        Collections.sort(years);
        CharSequence[] yearsCharSequences;
        if (includeAddYearsOption) {
            yearsCharSequences = new CharSequence[years.size()+1];
            yearsCharSequences[years.size()] = "Add year";
        }
        else
            yearsCharSequences = new CharSequence[years.size()];

        for (int i=0;i<years.size();i++)
            yearsCharSequences[i] = years.get(i);

        return yearsCharSequences;
    }

    protected CharSequence[] getCurrentWriters(boolean includeAddWriterOption)
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        Set<String> writerSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getWriter()!=null)
                writerSet.add(""+comic.getYear());
        }

        ArrayList<String> writers = Utilities.getStringsFromSet(writerSet);
        Collections.sort(writers);
        CharSequence[] writerCharSequences;
        if (includeAddWriterOption) {
            writerCharSequences = new CharSequence[writers.size()+1];
            writerCharSequences[writers.size()] = "Add year";
        }
        else
            writerCharSequences = new CharSequence[writers.size()];

        for (int i=0;i<writers.size();i++)
            writerCharSequences[i] = writers.get(i);

        return writerCharSequences;
    }
}
