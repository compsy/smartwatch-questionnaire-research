package com.example.josetalito.questapp;

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
import android.support.v4.util.LruCache;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josetalito on 26/04/2016.
 */
public class TestActivity extends Activity {

    static int NUM_ROWS = 1;
    static int NUM_COLS = 1;

    static int QUEST_GRID[][];

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "WearQuesActivity";
    private Questionnaire q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        Intent intent = getIntent();
        q = (Questionnaire) intent.getSerializableExtra("questionnaire");

        // TODO: Really useful later on?
        NUM_ROWS = q.getQuestions().size();
        QUEST_GRID = new int[NUM_ROWS][];
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

    private class MainAdapter extends FragmentGridPagerAdapter {
        private static final int TRANSITION_DURATION_MILLIS = 100;

        private final Context mContext;
        private List<Row> mRows;
        private ColorDrawable mDefaultBg;

        private ColorDrawable mClearBg;

        public MainAdapter(Context ctx, FragmentManager fm) {
            super(fm);
            mContext = ctx;

            /* Dinamically construct the Questionnaire */
            /* Depending on the question, a particular fragment is created */
            mRows = new ArrayList<Row>();
            int i;
            int number_of_questions = q.getQuestions().size();
            for (i = 0; i < number_of_questions ; i++) {
                Question question;
                question = q.getQuestions().get(i);
                Bundle bd = new Bundle();
                bd.putString(CardFragment.KEY_TITLE, "Question " + i + 1);
                bd.putString(CardFragment.KEY_TEXT, question.toString());
                CardFragment cd = new CardFragment();
                cd.setArguments(bd);
                /*int j;
                int number_of_choices = question.getChoices().size();
                ArrayList<CardFragment> answers = new ArrayList<CardFragment>(number_of_choices);
                for (j = 0; j < number_of_choices; j++) {
                    Bundle bdAnswer = new Bundle();
                    bd.putString(CardFragment.KEY_TEXT, question.getChoices().get(j).toString());
                    CardFragment answer = new CardFragment();
                    answer.setArguments(bdAnswer);
                    answers.add(answer);
                }*/
                TypeOfQuestion type = question.getType();
                switch(type) {
                    case FEW_ANSWERS:
                        CustomFragment cd_few_answers = new CustomFragment();
                        mRows.add(new Row(cd, cd_few_answers));
                        break;
                    case SLIDER:
                        // TODO: create layout for Slider
                        //mRows.add(new Row(cd, slider));
                        break;
                    default: // MANY_ANSWERS
                        mRows.add(new Row(cd));
                }

            }
            // TODO: to change to new Library function
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

        LruCache<Point, Drawable> mPageBackgrounds = new LruCache<Point, Drawable>(3) {
            @Override
            protected Drawable create(final Point page) {
                if (page.y == 2 && page.x == 1) {
                    int resid = R.drawable.bugdroid_large;
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

        private Fragment cardFragment(int titleRes, int textRes) {
            Resources res = mContext.getResources();
            CardFragment fragment =
                    CardFragment.create(res.getText(titleRes), res.getText(textRes));
            // Add some extra bottom margin to leave room for the page indicator
            fragment.setCardMarginBottom(
                    res.getDimensionPixelSize(R.dimen.card_margin_bottom));
            return fragment;
        }

        /*static*/ final int[] BG_IMAGES = new int[] {
                R.color.blue,
                R.color.blue,
                R.color.blue,
                R.color.blue,
                R.color.blue,
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
            private static final String TAG = "Loader";
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

    /* TODO: public static Fragment inner class? */
}
