package com.comicviewer.cedric.comicviewer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.comicviewer.cedric.comicviewer.Model.Collection;
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

    private String mCurrentCollection;

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

    protected void showAddFilterDialogMultiOption(final String title, final CharSequence[] options, final MaterialDialog.ListCallbackMultiChoice callback, final boolean showAddOption)
    {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
                .title(title)
                .negativeText(mContext.getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(mContext))
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                        showAskNameDialog("Add new filter",
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                        CharSequence[] newOptions = new CharSequence[options.length + 1];
                                        newOptions[newOptions.length - 1] = charSequence;
                                        showAddFilterDialogMultiOption(title, newOptions, callback, showAddOption);
                                    }
                                });
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showSelectFilterTypeDialog(mCurrentCollection);
                    }
                })
                .positiveText(mContext.getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(mContext));

        if (showAddOption)
            dialog
                    .neutralColor(StorageManager.getAppThemeColor(mContext))
                    .neutralText("Add new...");

        if (options.length>0)
        {
            dialog
                    .items(options)
                    .itemsCallbackMultiChoice(null, callback);
        }
        else
        {
            dialog.content("No filters found yet...");
        }

        dialog.show();
    }

    protected void showAddFilterDialogSingleOption(String title, CharSequence[] options, MaterialDialog.ListCallback callback)
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
                        showSelectFilterTypeDialog(mCurrentCollection);
                    }
                })
                .show();
    }

    protected void showSelectFilterTypeDialog(final String collectionName)
    {
        mCurrentCollection = collectionName;
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
                                showSeriesListDialog();
                                break;
                            case 1:
                                showYearListDialog();
                                break;
                            case 2:
                                showFolderListDialog();
                                break;
                            case 3:
                                showWriterListDialog();
                                break;
                            case 4:
                                showPencillerListDialog();
                                break;
                            case 5:
                                showInkerListDialog();
                                break;
                            case 6:
                                showColoristListDialog();
                                break;
                            case 7:
                                showLettererListDialog();
                                break;
                            case 8:
                                showEditorListDialog();
                                break;
                            case 9:
                                showCoverArtistDialog();
                                break;
                            case 10:
                                showStoryArcListDialog();
                                break;
                            case 11:
                                showCharacterListDialog();
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

    private void showCharacterListDialog() {
        showAddFilterDialogMultiOption("Add character",
                getCurrentCharacters(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddCharacterToCollection(mContext,
                                mCurrentCollection,
                                Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                },
                true
        );
    }

    private void showStoryArcListDialog() {
        showAddFilterDialogMultiOption("Add story arc",
                getCurrentStoryArcs(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddStoryArcToCollection(mContext,
                                mCurrentCollection,
                                Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                },
                true
        );
    }

    private void showCoverArtistDialog() {
        showAddFilterDialogMultiOption("Add cover artist",
                getCurrentCoverArtists(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddCoverArtistsToCollection(mContext,
                                mCurrentCollection,
                                Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                },
                true
        );
    }

    private void showEditorListDialog() {
        showAddFilterDialogMultiOption("Add editors",
                getCurrentEditors(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddEditorToCollection(mContext,
                                mCurrentCollection,
                                Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                },
                true
        );
    }

    private void showLettererListDialog() {
        showAddFilterDialogMultiOption("Add letterers",
                getCurrentLetterers(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddLetterersToCollection(mContext,
                                mCurrentCollection,
                                Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                },
                true
        );
    }

    private void showColoristListDialog() {
        showAddFilterDialogMultiOption("Add colorists",
                getCurrentColorists(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddColoristsToCollection(mContext,
                                mCurrentCollection,
                                Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                },
                true
        );
    }

    private void showInkerListDialog() {
        showAddFilterDialogMultiOption("Add inkers",
                getCurrentInkers(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddInkersToCollection(mContext,
                                mCurrentCollection,
                                Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                },
                true
        );
    }

    private void showPencillerListDialog() {
        showAddFilterDialogMultiOption("Add pencillers",
                getCurrentPencillers(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddPencillersToCollection(mContext,
                                mCurrentCollection,
                                Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                },
                true
        );
    }

    protected void showSeriesListDialog()
    {
        final CharSequence[] series = getCurrentSeries();

        showAddFilterDialogMultiOption(
                "Select series",
                series, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {

                        CollectionActions.batchAddSeriesFilterToCollection(mContext, mCurrentCollection, Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);

                        return true;
                    }
                }, true);
    }

    protected void showAskNameDialog(String title, MaterialDialog.InputCallback inputCallback)
    {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(title)
                .negativeText(mContext.getString(R.string.cancel))
                .negativeColor(StorageManager.getAppThemeColor(mContext))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showSelectFilterTypeDialog(mCurrentCollection);
                    }
                })
                .input("Filter name", "", false, inputCallback)
                .positiveText(mContext.getString(R.string.confirm))
                .positiveColor(StorageManager.getAppThemeColor(mContext))
                .show();
    }

    protected void showYearListDialog()
    {
        final CharSequence[] years = getCurrentYears();

        showAddFilterDialogMultiOption(
                "Select year",
                years,
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddYearsFilterToCollection(mContext, mCurrentCollection, Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                }, true);
    }

    protected void showFolderListDialog()
    {
        showAddFilterDialogMultiOption(
                "Select folders",
                getCurrentFolders(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddFolderFilterToCollection(mContext, mCurrentCollection, Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);
                        return true;
                    }
                }, false);
    }

    private void showWriterListDialog() {
        showAddFilterDialogMultiOption("Add writers",
                getCurrentWriters(),
                new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        CollectionActions.batchAddWriterFilterToCollection(mContext,
                                mCurrentCollection,
                                Utilities.charSequenceArrayToStringList(charSequences));
                        showSelectFilterTypeDialog(mCurrentCollection);

                        return true;
                    }
                },
                true
        );
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

        return Utilities.stringListToCharSequenceArray(folders);
    }

    protected CharSequence[] getCurrentSeries()
    {
        List<Comic> comics = ComicActions.getAllSimpleComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> seriesSet = new HashSet<>();

        for (Comic comic:comics) {
            if (!comic.getEditedTitle().trim().equals(""))
                seriesSet.add(comic.getEditedTitle());
        }

        for (Collection collection:collections)
        {
            for (String series:collection.getSeriesFilters())
                seriesSet.add(series);
        }

        ArrayList<String> series = Utilities.getStringsFromSet(seriesSet);
        Collections.sort(series);

        return Utilities.stringListToCharSequenceArray(series);
    }

    protected CharSequence[] getCurrentYears()
    {
        List<Comic> comics = ComicActions.getAllSimpleComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> yearsSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getEditedYear()!=-1)
                yearsSet.add(""+comic.getEditedYear());
        }

        for (Collection collection:collections)
        {
            for (Integer year:collection.getYearsFilters())
                yearsSet.add(""+year);
        }

        ArrayList<String> years = Utilities.getStringsFromSet(yearsSet);
        Collections.sort(years);

        return Utilities.stringListToCharSequenceArray(years);
    }

    protected CharSequence[] getCurrentWriters()
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> writerSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getWriter()!=null)
                writerSet.add(""+comic.getWriter());
        }
        for (Collection collection:collections)
        {
            for (String writer:collection.getWriterFilters())
                writerSet.add(writer);
        }

        ArrayList<String> writers = Utilities.getStringsFromSet(writerSet);
        Collections.sort(writers);

        return Utilities.stringListToCharSequenceArray(writers);
    }

    protected CharSequence[] getCurrentPencillers()
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> pencillerSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getPenciller()!=null)
                pencillerSet.add(""+comic.getPenciller());
        }

        for (Collection collection:collections)
        {
            for (String penciller:collection.getPencillerFilters())
                pencillerSet.add(penciller);
        }

        ArrayList<String> pencillers = Utilities.getStringsFromSet(pencillerSet);
        Collections.sort(pencillers);

        return Utilities.stringListToCharSequenceArray(pencillers);
    }

    protected CharSequence[] getCurrentInkers()
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> inkerSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getInker()!=null)
                inkerSet.add(""+comic.getInker());
        }

        for (Collection collection:collections)
        {
            for (String inker:collection.getInkerFilters())
                inkerSet.add(inker);
        }

        ArrayList<String> inkers = Utilities.getStringsFromSet(inkerSet);
        Collections.sort(inkers);

        return Utilities.stringListToCharSequenceArray(inkers);
    }

    protected CharSequence[] getCurrentColorists()
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> coloristsSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getColorist()!=null)
                coloristsSet.add(""+comic.getColorist());
        }

        for (Collection collection:collections)
        {
            for (String colorist:collection.getColoristFilters())
                coloristsSet.add(colorist);
        }

        ArrayList<String> colorists = Utilities.getStringsFromSet(coloristsSet);
        Collections.sort(colorists);

        return Utilities.stringListToCharSequenceArray(colorists);
    }

    protected CharSequence[] getCurrentLetterers()
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> lettererSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getLetterer()!=null)
                lettererSet.add(""+comic.getLetterer());
        }

        for (Collection collection:collections)
        {
            for (String letterer:collection.getLettererFilters())
                lettererSet.add(letterer);
        }

        ArrayList<String> letterers = Utilities.getStringsFromSet(lettererSet);
        Collections.sort(letterers);

        return Utilities.stringListToCharSequenceArray(letterers);
    }

    protected CharSequence[] getCurrentEditors()
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> editorSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getEditor()!=null)
                editorSet.add(""+comic.getEditor());
        }

        for (Collection collection:collections)
        {
            for (String editor:collection.getEditorFilters())
                editorSet.add(editor);
        }

        ArrayList<String> editors = Utilities.getStringsFromSet(editorSet);
        Collections.sort(editors);

        return Utilities.stringListToCharSequenceArray(editors);
    }

    protected CharSequence[] getCurrentCoverArtists()
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> coverArtistSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getCoverArtist()!=null)
                coverArtistSet.add(""+comic.getCoverArtist());
        }

        for (Collection collection:collections)
        {
            for (String coverArtist:collection.getCoverArtistFilters())
                coverArtistSet.add(coverArtist);
        }

        ArrayList<String> coverArtists = Utilities.getStringsFromSet(coverArtistSet);
        Collections.sort(coverArtists);

        return Utilities.stringListToCharSequenceArray(coverArtists);
    }

    protected CharSequence[] getCurrentStoryArcs()
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> storyArcSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getStoryArcs()!=null) {
                for (int i=0;i<comic.getStoryArcs().size();i++)
                    storyArcSet.add("" + comic.getStoryArcs().get(i));
            }
        }

        for (Collection collection:collections)
        {
            for (String storyArc:collection.getStoryArcsFilters())
                storyArcSet.add(storyArc);
        }

        ArrayList<String> storyArcs = Utilities.getStringsFromSet(storyArcSet);
        Collections.sort(storyArcs);

        return Utilities.stringListToCharSequenceArray(storyArcs);
    }

    protected CharSequence[] getCurrentCharacters()
    {
        List<Comic> comics = StorageManager.getSavedComics(mContext);
        List<Collection> collections = StorageManager.getCollectionList(mContext);

        Set<String> characterSet = new HashSet<>();

        for (Comic comic:comics) {
            if (comic.getCharacters()!=null) {
                for (int i=0;i<comic.getCharacters().size();i++)
                    characterSet.add("" + comic.getCharacters().get(i));
            }
        }
        for (Collection collection:collections)
        {
            for (String character:collection.getCharactersFilters())
                characterSet.add(character);
        }

        ArrayList<String> characters = Utilities.getStringsFromSet(characterSet);
        Collections.sort(characters);

        return Utilities.stringListToCharSequenceArray(characters);
    }
}
