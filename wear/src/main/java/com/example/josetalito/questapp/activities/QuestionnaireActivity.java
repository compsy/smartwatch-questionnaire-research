package com.example.josetalito.questapp.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.LruCache;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;

import com.example.common.TypeOfQuestion;
import com.example.common.model.Question;
import com.example.common.model.Questionnaire;
import com.example.common.model.Solutions;
import com.example.josetalito.questapp.R;
import com.example.josetalito.questapp.fragments.FewAnswersFragment;
import com.example.josetalito.questapp.fragments.FinishFragment;
import com.example.josetalito.questapp.fragments.OnDataPass;
import com.example.josetalito.questapp.fragments.manyanswersfragment.ManyAnswersFragment;
import com.example.josetalito.questapp.fragments.SliderFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josetalito on 26/04/2016.
 */
public class QuestionnaireActivity extends Activity implements OnDataPass {

    int NUM_ROWS;
    int QUEST_GRID[][];

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "WearQuesActivity";

    /**
     * Google Api Client. Interface to use the Wearable Api (from Google Play Services).
     */
    private GoogleApiClient mGoogleApiClient;

    private Questionnaire q;
    private int NOTIFICATION_ID;

    /**
     *  Storage of the solutions.
     */
    Solutions solutions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        /** Initializing the Google API Client for sending back the answers to the handheld **/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        /** Initializing the questionnaire, solutions and preparing for building the dynamic View
         * of the questionnaire.
         */
        Intent intent = getIntent();

        q = (Questionnaire) intent.getSerializableExtra("questionnaire");
        NOTIFICATION_ID = intent.getIntExtra("NOTIFICATION_ID", -1);
        if (NOTIFICATION_ID == -1) {
            Log.e(TAG, "ERROR: NOTIFICATION_ID == -1.");
        }
        NUM_ROWS = q.getQuestions().size();
        QUEST_GRID = new int[NUM_ROWS][];

        // we register a potential solution for the current questionnaire
        setSolutions(new Solutions(q.getQuestionnaireKey(), NUM_ROWS));

        int i;
        for(i = 0; i < NUM_ROWS ; i++) {
            // get the number of choices per question
            int j = q.getQuestions().get(i).getChoices().size();
            QUEST_GRID[i] = new int[j];
        }

        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Adjust page margins:
                //   A little extra horizontal spacing between pages looks a bit
                //   less crowded on a round display.
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                pager.setPageMargins(rowMargin, colMargin);

                // GridViewPager relies on insets to properly handle
                // layout for round displays. They must be explicitly
                // applied since this listener has taken them over.
                pager.onApplyWindowInsets(insets);
                return insets;
            }
        });
        pager.setAdapter(new MainAdapter(this, getFragmentManager()));
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);
    }

    /**
     * Called by the Fragments (questions).
     */
    public void setSolutions(Solutions solutions) {
        this.solutions = solutions;
    }

    @Override
    public void onDataPass(int questionID, String data) {
        solutions.getSolutions().put(questionID, data);
        Log.i(TAG, "onDataPass called. Question: " + questionID + " with choice: " + data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // We do not need the notification anymore, dismiss it
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
        // And now send the answers to the handheld
        if (mGoogleApiClient.isConnected()) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/path/to/data");
            dataMapRequest.getDataMap().putString("QUESTIONNAIRE_KEY", q.getQuestionnaireKey());

            // Serializing the questionnaire
            byte[] solutionsBytes = SerializationUtils.serialize(solutions);
            dataMapRequest.getDataMap().putByteArray("answers", solutionsBytes);

            dataMapRequest.setUrgent(); // We want it to be delivered straight away
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        }
        else {
            Log.e(TAG, "No connection to handheld available!");
        }
        Log.i(TAG, "Notification removed.");
    }


    /**
     * Dynamic creation of the QuestionnaireActivity View
     */
    private class MainAdapter extends FragmentGridPagerAdapter {
        private static final int TRANSITION_DURATION_MILLIS = 100;

        private final Context mContext;
        private List<Row> mRows;
        private ColorDrawable mDefaultBg;

        private ColorDrawable mClearBg;

        public MainAdapter(Context ctx, FragmentManager fm) {
            super(fm);
            mContext = ctx;

            /* Dynamically construct the Questionnaire */
            /* Depending on the question, a particular fragment is created
             * Every Row is a different Question (in particular, the TEXT of a question).
            */
            mRows = new ArrayList<>();
            int i;
            int number_of_questions = q.getQuestions().size();

            for (i = 0; i < number_of_questions ; i++) {
                Bundle bd = new Bundle();
                Question question;
                CardFragment cd = new CardFragment();
                question = q.getQuestions().get(i);

                bd.putString(CardFragment.KEY_TITLE, "Question " + (i+1));
                bd.putString(CardFragment.KEY_TEXT, question.toString());
                bd.putSerializable("question", question);

                cd.setArguments(bd);
                /**
                 * Depending on the type of the question, a particular number of columns
                 * are added to that row. A COLUMN is represented as an extra parameter when
                 * creating the Row. For example, a ROW (question) with two COLUMNS (solutions)
                 * is "mRows.add(new Row(cd, cd_few_answers));"
                 */
                TypeOfQuestion type = question.getType();
                switch(type) {
                    case FEW_ANSWERS:
                        FewAnswersFragment cd_few_answers = new FewAnswersFragment();
                        cd_few_answers.setArguments(bd);
                        mRows.add(new Row(cd, cd_few_answers));
                        break;
                    case SLIDER:
                        SliderFragment cd_slider = new SliderFragment();
                        cd_slider.setArguments(bd);
                        mRows.add(new Row(cd, cd_slider));
                        break;
                    default: // MANY_ANSWERS
                        ManyAnswersFragment cd_many = new ManyAnswersFragment();
                        cd_many.setArguments(bd);
                        mRows.add(new Row(cd, cd_many));
                    /*default: // MANY ANSWERS - Several pages // TODO: messy - prototyping another layout
                        Log.i(TAG, "Many answers switch");
                        Bundle bdMany = new Bundle();
                        bdMany.putSerializable("choices", question.getChoices());
                        bdMany.putInt("limit", 0);
                        FewAnswersFragment_ManyPages cd_many = new FewAnswersFragment_ManyPages();
                        cd_many.setArguments(bdMany);
                        Bundle bdMany2 = new Bundle();
                        bdMany2.putSerializable("choices", question.getChoices());
                        bdMany2.putInt("limit", 1);
                        FewAnswersFragment_ManyPages cd_many2 = new FewAnswersFragment_ManyPages();
                        cd_many2.setArguments(bdMany2);
                        mRows.add(new Row(cd, cd_many, cd_many2));*/
                }

            }

            FinishFragment finishQ = new FinishFragment();
            mRows.add(new Row(finishQ));

            mDefaultBg = new ColorDrawable(getResources().getColor(R.color.dark_grey));
            mClearBg = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        }

        LruCache<Integer, Drawable> mRowBackgrounds = new LruCache<Integer, Drawable>(3) {
            @Override
            protected Drawable create(final Integer row) {
                int resid = BG_IMAGES[row % BG_IMAGES.length];
                new DrawableLoadingTask(mContext) {
                    @Override
                    protected void onPostExecute(Drawable result) {
                        TransitionDrawable background = new TransitionDrawable(new Drawable[] {
                                mDefaultBg,
                                result
                        });
                        mRowBackgrounds.put(row, background);
                        notifyRowBackgroundChanged(row);
                        background.startTransition(TRANSITION_DURATION_MILLIS);
                    }
                }.execute(resid);
                return mDefaultBg;
            }
        };

        // TODO: to change
        LruCache<Point, Drawable> mPageBackgrounds = new LruCache<Point, Drawable>(3) {
            @Override
            protected Drawable create(final Point page) {
                if (page.y == 2 && page.x == 1) {
                    int resid = R.drawable.background;
                    new DrawableLoadingTask(mContext) {
                        @Override
                        protected void onPostExecute(Drawable result) {
                            TransitionDrawable background = new TransitionDrawable(new Drawable[] {
                                    mClearBg,
                                    result
                            });
                            mPageBackgrounds.put(page, background);
                            notifyPageBackgroundChanged(page.y, page.x);
                            background.startTransition(TRANSITION_DURATION_MILLIS);
                        }
                    }.execute(resid);
                }
                return GridPagerAdapter.BACKGROUND_NONE;
            }
        };

        final int[] BG_IMAGES = new int[] {
                R.color.background_color,
                R.color.background_color,
                R.color.background_color,
                R.color.background_color,
                R.color.background_color,
                R.color.background_color,
        };

        /** A convenient container for a row of fragments. */
        private class Row {
            final List<Fragment> columns = new ArrayList<Fragment>();

            public Row(Fragment... fragments) {
                for (Fragment f : fragments) {
                    add(f);
                }
            }

            public void add(Fragment f) {
                columns.add(f);
            }

            Fragment getColumn(int i) {
                return columns.get(i);
            }

            public int getColumnCount() {
                return columns.size();
            }
        }

        @Override
        public Fragment getFragment(int row, int col) {
            Row adapterRow = mRows.get(row);
            return adapterRow.getColumn(col);
        }

        // TODO
        @Override
        public Drawable getBackgroundForRow(final int row) {
            return mRowBackgrounds.get(row);
        }

        // TODO
        @Override
        public Drawable getBackgroundForPage(final int row, final int column) {
            return mPageBackgrounds.get(new Point(column, row));
        }

        @Override
        public int getRowCount() {
            return mRows.size();
        }

        @Override
        public int getColumnCount(int rowNum) {
            return mRows.get(rowNum).getColumnCount();
        }

        class DrawableLoadingTask extends AsyncTask<Integer, Void, Drawable> {
            private static final String TAG = "LoaderQActivity";
            private Context context;

            DrawableLoadingTask(Context context) {
                this.context = context;
            }

            @Override
            protected Drawable doInBackground(Integer... params) {
                Log.d(TAG, "Loading asset 0x" + Integer.toHexString(params[0]));
                return context.getResources().getDrawable(params[0]);
            }
        }
    }

}