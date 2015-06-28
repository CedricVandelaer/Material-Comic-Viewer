package com.comicviewer.cedric.comicviewer.ComicListFiles;

import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import com.comicviewer.cedric.comicviewer.DrawerActivity;
import com.comicviewer.cedric.comicviewer.FileLoader;
import com.comicviewer.cedric.comicviewer.NavigationManager;
import com.comicviewer.cedric.comicviewer.PreferenceFiles.PreferenceSetter;
import com.comicviewer.cedric.comicviewer.R;
import com.comicviewer.cedric.comicviewer.SearchFilter;
import com.comicviewer.cedric.comicviewer.Utilities;

import java.io.File;
import java.util.Map;

/**
 * Created by CV on 22/06/2015.
 */
public class ComicListFragment extends AbstractComicListFragment {

    private static ComicListFragment mSingleton;

    public static ComicListFragment getInstance()
    {
        if (mSingleton== null)
            mSingleton = new ComicListFragment();
        return mSingleton;
    }

    public ComicListFragment()
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (PreferenceSetter.getFolderEnabledSetting(mApplicationContext))
        {
            if (NavigationManager.getInstance().fileStackEmpty())
            {
                mAdapter.clearList();
                NavigationManager.getInstance().resetFileStack();
            }
        }
    }

    @Override
    void setSearchFilters() {

        mFilters.clear();

        mFilters.add(new SearchFilter(PreferenceSetter.getFolderEnabledSetting(getActivity())) {
            @Override
            public boolean compare(Object object) {
                return !(object instanceof File) || mCompareSetting;
            }
        });
    }

    @Override
    void addShowFolderViewButton(boolean enable) {
        if (enable && getActivity()!=null) {
            final Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
            toolbar.removeView(mFolderViewToggleButton);
            mFolderViewToggleButton = new ImageButton(getActivity());
            mFolderViewToggleButton.setAlpha(0.75f);
            if (Build.VERSION.SDK_INT>15)
                mFolderViewToggleButton.setBackground(null);
            else
                mFolderViewToggleButton.getBackground().setAlpha(0);

            if (PreferenceSetter.getFolderEnabledSetting(getActivity()))
            {
                mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
            }
            else
            {
                mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
            }

            mFolderViewToggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreferenceSetter.getFolderEnabledSetting(getActivity())) {
                        PreferenceSetter.setFolderEnabledSetting(getActivity(), false);
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
                    } else {
                        PreferenceSetter.setFolderEnabledSetting(getActivity(), true);
                        mFolderViewToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                    }
                    NavigationManager.getInstance().resetFileStack();
                    refresh();

                }
            });

            final Toolbar.LayoutParams layoutParamsCollapsed = new Toolbar.LayoutParams(Gravity.RIGHT);
            toolbar.addView(mFolderViewToggleButton, layoutParamsCollapsed);
        }
        else
        {
            if (getActivity()!=null) {
                Toolbar toolbar = ((DrawerActivity) getActivity()).getToolbar();
                toolbar.removeView(mFolderViewToggleButton);
            }
        }
        setSearchFilters();
    }

    @Override
    public Map<String, String> getFiles() {
        if (PreferenceSetter.getFolderEnabledSetting(mApplicationContext))
            return FileLoader.searchComicsAndFolders(mApplicationContext, NavigationManager.getInstance().getPathFromFileStack());
        else
            return FileLoader.searchComics(mApplicationContext);
    }


    /*
    @Override
    void searchComicsAndFolders() {
        if (NavigationManager.getInstance().fileStackEmpty())
            return;
        //map of <filename, filepath>
        Map<String,String> map = FileLoader.searchComicsAndFolders(mApplicationContext, NavigationManager.getInstance().getPathFromFileStack());

        TreeMap<String, String> treemap = new TreeMap<>(map);

        List<Object> currentObjects = mAdapter.getComicsAndFiles();
        ArrayList<Comic> savedComics = PreferenceSetter.getSavedComics(mApplicationContext);
        List<String> savedComicsFileNames = new ArrayList<>();

        for (int i=0;i<savedComics.size();i++)
        {
            savedComicsFileNames.add(savedComics.get(i).getFilePath()+"/"+savedComics.get(i).getFileName());
        }

        List<String> currentComicsFileNames = new ArrayList<>();
        List<String> currentFolderNames = new ArrayList<>();

        for (int i=0;i<currentObjects.size();i++)
        {
            if (currentObjects.get(i) instanceof Comic) {
                Comic comic = (Comic) currentObjects.get(i);
                currentComicsFileNames.add(comic.getFilePath() + "/" + comic.getFileName());
            }
            else if(currentObjects.get(i) instanceof File)
            {
                File folder = (File) currentObjects.get(i);
                currentFolderNames.add(folder.getName());
            }
        }

        final ArrayList<Comic> comicsToSave = new ArrayList<>();
        final Set<String> comicsToAdd = new HashSet<>();

        boolean hasToLoad = false;

        for (String str:treemap.keySet())
        {
            if (mSearchComicsTask!= null && mSearchComicsTask.isCancelled())
                break;

            //open the new found file
            final String comicPath = map.get(str)+"/"+str;
            final File file = new File(comicPath);

            //check for image folder
            if (file.isDirectory() && Utilities.checkImageFolder(file) && !(currentComicsFileNames.contains(comicPath))) {
                Comic comic = new Comic(file.getName(), file.getParentFile().getAbsolutePath());

                ComicLoader.loadComicSync(mApplicationContext, comic);

                comicsToAdd.add(comic.getFileName());

                if (ComicLoader.setComicColor(mApplicationContext, comic)) {
                    comicsToSave.add(comic);
                    hasToLoad = true;
                }

                if (!hasToLoad)
                {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.scrollToPosition(0);
                        }
                    });
                }

                final Comic finalComic = comic;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addObjectSorted(finalComic);
                    }
                });

            }
            else if (file.isDirectory() && !(currentFolderNames.contains(file.getName())) && !(currentComicsFileNames.contains(comicPath))) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addObjectSorted(file);
                        mRecyclerView.scrollToPosition(0);
                    }
                });

            }//check if comic is one of the saved comic files and add
            else if (savedComicsFileNames.contains(comicPath) && !(currentComicsFileNames.contains(comicPath)))
            {

                int pos = savedComicsFileNames.indexOf(comicPath);

                Comic comic = savedComics.get(pos);

                comicsToAdd.add(comic.getFileName());

                ComicLoader.generateComicInfo(mApplicationContext, comic);

                if (ComicLoader.setComicColor(mApplicationContext, comic)) {
                    comicsToSave.add(comic);
                    hasToLoad = true;
                }

                if (!hasToLoad)
                {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.scrollToPosition(0);
                        }
                    });
                }

                final Comic finalComic = comic;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addObjectSorted(finalComic);
                    }
                });

            }//if it is a newly added comic
            else if (!(currentComicsFileNames.contains(comicPath))
                    && Utilities.checkExtension(str)
                    && (Utilities.isZipArchive(file) || Utilities.isRarArchive(file))) {

                Comic comic = new Comic(str, map.get(str));

                ComicLoader.loadComicSync(mApplicationContext, comic);

                comicsToAdd.add(comic.getFileName());

                final Comic finalComic = comic;

                hasToLoad = true;

                comicsToSave.add(comic);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addObjectSorted(finalComic);
                    }
                });

            }
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                PreferenceSetter.batchSaveComics(mApplicationContext, comicsToSave);
                PreferenceSetter.batchAddAddedComics(mApplicationContext, comicsToAdd);
            }
        }).run();

        updateLastReadComics();

    }*/

}
