package com.satti.android.todo.frag;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.satti.android.todo.AddOrEditATaskActivity;
import com.satti.android.todo.R;
import com.satti.android.todo.SearchActivity;
import com.satti.android.todo.StartUpActivity;
import com.satti.android.todo.adapter.CustomArrayAdapter;
import com.satti.android.todo.db.TodoDao;
import com.satti.android.todo.model.Task;
import com.satti.android.todo.util.Log;
import com.satti.android.todo.util.ProgressUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ToDoListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ToDoListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageView mFabAddButton;
    private RetrieveAllTasks mRetrieveAllTasks;
    CustomArrayAdapter mArrayAdapter;
    ListView mListView;
    List<Task> mTaskList;
    TodoDao mTodoDao;
    Activity mActivityContext;
    public ToDoListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToDoListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDoListFragment newInstance(String param1, String param2) {
        ToDoListFragment fragment = new ToDoListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivityContext = activity;
      //  Log.i("ToDoListFragment onAttach END");

        //       if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Log.i("ToDoListFragment onCreate END");

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_to_do_list, container, false);
        mListView = (ListView)view.findViewById(R.id.task_listview);
        mTodoDao = new TodoDao(mActivityContext.getApplicationContext());
        mTaskList = new ArrayList<Task>();
        mArrayAdapter = new CustomArrayAdapter(getActivity(),R.layout.listitem, mTaskList,false);
        mListView.setEmptyView(view.findViewById(R.id.list_empty_view));
        mListView.setAdapter(mArrayAdapter);
        mArrayAdapter.notifyDataSetChanged();
        mFabAddButton = (ImageView)view.findViewById(R.id.fab_add);
        mFabAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskIntent = new Intent(getActivity(),AddOrEditATaskActivity.class);
                addTaskIntent.putExtra("isFromAdd",true);
                startActivity(addTaskIntent);
            }
        });
       // Log.i("ToDoListFragment onCreateView END");

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mRetrieveAllTasks == null) {
            Log.d("ToDoListFragment onStart || mRetrieveAllTasks NULL");
            mRetrieveAllTasks = new RetrieveAllTasks();
            mRetrieveAllTasks.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            Log.d("ToDoListFragment onStart || mRetrieveAllTasks NOT NULL");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.isAdded()) {
          //  Log.i("ToDoListFragment onResume");

        }
    }


    @Override
    public void onPause() {
        super.onPause();
//        Log.i("ToDoListFragment onPause");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
  //      Log.i("ToDoListFragment onSaveInstanceState");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mActivityContext.getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    //    Log.i("ToDoListFragment onCreateOptionsMenu END");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.menu_sort_by_date:
                handleSortOPtions("date");
                return true;
            case R.id.menu_sort_by_name:
                handleSortOPtions("name");
                return true;
            case R.id.menu_sort_by_priority:
                handleSortOPtions("priority");
                return true;
            case R.id.menu_search:
                Intent searchIntent = new Intent(mActivityContext,SearchActivity.class);
                startActivity(searchIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO ,move this code to background,to avoid ANR in low end devices !
    public void handleSortOPtions(String sortCriteria){
        List<Task> mTempList = mTodoDao.getAllTasksByCriteria(sortCriteria);
        if(mTempList != null && mTempList.size() > 0){
            mTaskList.clear(); //clear the old data
            for(int i=0 ; i < mTempList.size() ;i++){
                mTaskList.add(mTempList.get(i));
            }
            mArrayAdapter.notifyDataSetChanged();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDestroy() {
        if(mRetrieveAllTasks != null){
            mRetrieveAllTasks.cancel(true);
            mRetrieveAllTasks = null;
        }
        super.onDestroy();
      //  Log.i("ToDoListFragment onDestroy END");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        //Log.i("ToDoListFragment onDetach END");

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private  class RetrieveAllTasks extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mActivityContext != null){
             //   Log.i("RetrieveAllTasks onPreExecute");

                ProgressUtil.displayProgressDialog(mActivityContext);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mTaskList != null){
                mTaskList.clear(); //make sure , remove any old data
            }
            List<Task> mTempTaskList = mTodoDao.getAllTasks();
            for(int i=0 ; i < mTempTaskList.size() ;i++){
                mTaskList.add(mTempTaskList.get(i));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
         //   Log.i("RetrieveAllTasks onPostExecute");

            ProgressUtil.hideProgressDialog();
            mRetrieveAllTasks = null;//to make sure to reload the data again !!!


            if(mArrayAdapter != null){
                mArrayAdapter.notifyDataSetChanged();
            }
//            if(mListView != null){
//                mListView.setSelection(0);
//            }
        }
    }
}
