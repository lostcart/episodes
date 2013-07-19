
package lost.cart.apps.upcomingepisodes;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import lost.cart.apps.upcomingepisodes.data.ContentDescriptor;
import lost.cart.apps.upcomingepisodes.data.Episodes.Episode;

/**
 * A fragment representing a single Episode detail screen. This fragment is
 * either contained in a {@link EpisodeListActivity} in two-pane mode (on
 * tablets) or a {@link EpisodeDetailActivity} on handsets.
 */
public class EpisodeDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private Episode mItem;

    private static final int DETAILS_ID = 1;

    private TextView mTitle, mDescription, mAirDate;

    private ImageView mImageView;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EpisodeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            this.getLoaderManager().initLoader(DETAILS_ID, getArguments(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_episode_detail, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.episode_title);
        mDescription = (TextView) rootView.findViewById(R.id.episode_description);
        mAirDate = (TextView) rootView.findViewById(R.id.episode_airdate);
        mImageView = (ImageView) rootView.findViewById(R.id.episode_image);

        return rootView;
    }

    public void setupViews() {
        if (mItem != null) {
            mTitle.setText(mItem.mTitle);
            mDescription.setText(mItem.mDescription);
            mAirDate.setText(mItem.mAirDate);
            imageLoader.displayImage(mItem.mImageUrl, mImageView);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        switch (arg0) {
            case DETAILS_ID:
                CursorLoader CL = new CursorLoader(getActivity(),
                        ContentDescriptor.Episode.CONTENT_URI, null,
                        ContentDescriptor.Episode.Cols.ID + "=" + arg1.getInt(ARG_ITEM_ID), null,
                        null);
                return CL;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        if (arg1 != null) {
            mItem = new Episode(arg1);
            setupViews();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
}
