
package lost.cart.apps.upcomingepisodes;

import com.manuelpeinado.refreshactionitem.ProgressIndicatorType;
import com.manuelpeinado.refreshactionitem.RefreshActionItem;
import com.manuelpeinado.refreshactionitem.RefreshActionItem.RefreshActionListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

import lost.cart.apps.upcomingepisodes.data.ContentDescriptor;
import lost.cart.apps.upcomingepisodes.data.GetEpisodesService;

/**
 * A list fragment representing a list of Episodes. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link EpisodeDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class EpisodeListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, RefreshActionListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Identifier for the cursor loader
     */
    private static final int EPISODE_LIST = 1;
    /**
     * ActionBar item to show refresh state.
     */
    private RefreshActionItem mRefreshActionItem;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }
    };

    private SimpleCursorAdapter mAdapter;

    DisplayImageOptions mImageLoaderOptions;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public class EpisodeBinder implements ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int arg2) {
            switch (view.getId()) {
                case R.id.episode_image: {
                    ImageView image_view = (ImageView) view;
                    String image = cursor.getString(cursor
                            .getColumnIndex(ContentDescriptor.Episode.Cols.IMAGE));
                    imageLoader.displayImage(image, image_view, mImageLoaderOptions);
                    return true;
                }
                default:
                    return false;
            }

        }
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EpisodeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.episode_list_row, null,
                new String[] {
                        ContentDescriptor.Episode.Cols.TITLE,
                        ContentDescriptor.Episode.Cols.DESCRIPTION,
                        ContentDescriptor.Episode.Cols.AIR_DATE,
                        ContentDescriptor.Episode.Cols.IMAGE
                }, new int[] {
                        R.id.episode_title, R.id.episode_description, R.id.episode_airdate,
                        R.id.episode_image
                }, 0);
        setListAdapter(mAdapter);

        this.getLoaderManager().initLoader(EPISODE_LIST, null, this);

        mImageLoaderOptions = initImageLoader(this.getActivity());

        mAdapter.setViewBinder(new EpisodeBinder());
    }
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.fragment_episode_list, menu);
		MenuItem item = menu.findItem(R.id.refresh);
		mRefreshActionItem = (RefreshActionItem) item.getActionView();
		mRefreshActionItem.setMenuItem(item);
		mRefreshActionItem
				.setProgressIndicatorType(ProgressIndicatorType.INDETERMINATE);
		mRefreshActionItem.setRefreshActionListener(this);

	}
	
	@Override
	public void onRefreshButtonClick(RefreshActionItem sender) {
		refreshData();

	}

    private void refreshData() {
    	if(mRefreshActionItem != null){
    		mRefreshActionItem.showProgress(true);	
    	}
        final Intent msgIntent = new Intent(this.getActivity(), GetEpisodesService.class);
        getActivity().startService(msgIntent);
		
	}

	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        Cursor temp = mAdapter.getCursor();
        if (temp.moveToPosition(position)) {
            int temp_id = temp.getInt(temp.getColumnIndex(ContentDescriptor.Episode.Cols.ID));
            mCallbacks.onItemSelected(temp_id);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        switch (arg0) {
            case EPISODE_LIST:
                CursorLoader CL = new CursorLoader(getActivity(),
                        ContentDescriptor.Episode.CONTENT_URI, null, null, null, null);
                return CL;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        if (arg1 != null) {
        	if(mRefreshActionItem != null){
        		mRefreshActionItem.showProgress(false);
        	}
            mAdapter.swapCursor(arg1);
        } else {
            // No current results so try and get some..
        	refreshData();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {

    }

    private DisplayImageOptions initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
    }
}
