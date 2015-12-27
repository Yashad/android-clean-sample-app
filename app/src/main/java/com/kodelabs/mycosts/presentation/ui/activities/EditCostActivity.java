package com.kodelabs.mycosts.presentation.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.Toast;

import com.kodelabs.mycosts.MainThreadImpl;
import com.kodelabs.mycosts.R;
import com.kodelabs.mycosts.domain.interactors.base.ThreadExecutor;
import com.kodelabs.mycosts.domain.model.Cost;
import com.kodelabs.mycosts.presentation.presenters.EditCostPresenter;
import com.kodelabs.mycosts.presentation.presenters.impl.EditCostPresenterImpl;
import com.kodelabs.mycosts.utils.DateUtils;

/**
 * Created by dmilicic on 12/27/15.
 */
public class EditCostActivity extends AbstractCostActivity implements EditCostPresenter.View {

    private EditCostPresenter mPresenter;
    private Cost              mEditedCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create a presenter for this screen
        mPresenter = new EditCostPresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this);

        // extract the cost id of the item we want to edit
        long costId = getIntent().getLongExtra(MainActivity.EXTRA_COST_ID, -1);

        // in case cost id is not sent
        if (costId == -1) {
            Toast.makeText(this, "Cost not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // first get the old cost from the database
        mPresenter.getCostById(costId);
    }

    @Override
    public void onCostRetrieved(@NonNull Cost cost) {

        mEditedCost = cost;

        // populate the member variables
        mAmount = cost.getAmount();
        mSelectedDate = cost.getDate();
        mCategory = cost.getCategory();
        mDescription = cost.getDescription();

        prepopulateFields();
    }


    /**
     * Finds the position of the given category inside the spinner.
     *
     * @param category The provided category for which we are finding the position.
     * @return Returns an int which represents a position of the provided category in the spinner.
     */
    private int findCategoryPosition(String category) {
        int result = -1;
        String[] categoryArray = getResources().getStringArray(R.array.category_array);
        for (int i = 0; i < categoryArray.length; i++) {
            if (category.equals(categoryArray[i])) result = i;
        }

        return result;
    }

    private void prepopulateFields() {

        // prepopulate all text views
        mAmountEditText.setText(String.valueOf(mAmount));
        mDateTextView.setText(DateUtils.formatDate(mSelectedDate));
        mDescriptionEditText.setText(mDescription);

        // find the position of the category item to select
        int position = findCategoryPosition(mCategory);
        mCategorySpinner.setSelection(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {

            extractFormData();

            // pass the data onto the presenter
            mPresenter.editCost(mEditedCost, mSelectedDate, mAmount, mDescription, mCategory);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onCostUpdated(Cost cost) {
        Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String message) {

    }
}
