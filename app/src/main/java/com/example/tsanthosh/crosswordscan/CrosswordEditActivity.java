package com.example.tsanthosh.crosswordscan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.tsanthosh.crosswordscan.restclient.VolleyMultipartRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CrosswordEditActivity extends AppCompatActivity {

    private static final String UPLOAD_URL = "http://192.168.0.103:5000/api/detect-crossword";
    private Uri fileUri;
    private Bitmap bitmap;


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private List<List<Integer>> crosswordMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossword_edit);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            fileUri = (Uri) bundle.get("image_uri");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fileUri);


        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crossword_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static void uploadImage(final Uri fileUri, final Activity activity, final View rootview) {

        final String KEY_IMAGE = "image";
        final String KEY_NAME = "name";
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(activity, "Uploading...", "Please wait...", false, false);
        VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(activity, new String(response.data), Toast.LENGTH_LONG).show();

                        String json = new String(response.data);
                        Gson gson = new GsonBuilder().create();
                        List<List<Integer>> crosswordMatrix =gson.fromJson(json, new TypeToken<List<List<Integer>>>(){}.getType());


                        CustomView customView = (CustomView) rootview.findViewById(R.id.customView);
                        customView.setCrosswordMatrix(crosswordMatrix);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();


                        //Showing toast
                        Toast.makeText(activity, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters


                //returning parameters
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws IOException {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                InputStream iStream = null;
                try {
                    iStream = activity.getContentResolver().openInputStream(fileUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] inputData = getBytes(iStream);

                params.put("file", new DataPart("file_avatar.jpg", inputData));

                return params;
            }

            public byte[] getBytes(InputStream inputStream) throws IOException {
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];

                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                return byteBuffer.toByteArray();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "multipart/form-data");
                return headers;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_crossword_edit, container, false);

            return rootView;
        }
    }

    public static class CrosswordEditFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private  Uri fileUri;
        public CrosswordEditFragment( ) {
        }

        public static CrosswordEditFragment newInstance(Uri fileUri) {

            Bundle args = new Bundle();

            CrosswordEditFragment fragment = new CrosswordEditFragment();
            args.putString("file_uri", String.valueOf(fileUri));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_crossword_edit, container, false);


            fileUri = ((CrosswordEditActivity)getActivity()).fileUri;

            uploadImage(fileUri, getActivity(), rootView);
            String json = "[[1, 2, 3, 4, 5, -1, 6, 7, 8, 9, 10, -1, 11, 12, 13], [14, 0, 0, 0, 0, -1, 15, 0, 0, 0, 0, -1, 16, 0, 0], [17, 0, 0, 0, 0, 18, 0, 0, 0, 0, 0, -1, 19, 0, 0], [20, 0, 0, -1, 21, 0, 0, 0, 0, -1, -1, 22, 0, 0, 0], [-1, -1, -1, 23, 0, 0, 0, -1, 24, 25, 26, 0, 0, 0, 0], [27, 28, 29, 0, 0, 0, -1, -1, -1, 30, 0, 0, 0, -1, -1], [31, 0, 0, 0, -1, -1, 32, 33, 34, 0, 0, -1, 35, 36, 37], [38, 0, 0, -1, 39, 40, 0, 0, 0, 0, 0, -1, 41, 0, 0], [42, 0, 0, -1, 43, 0, 0, 0, 0, -1, -1, 44, 0, 0, 0], [-1, -1, 45, 46, 0, 0, -1, -1, -1, 47, 48, 0, 0, 0, 0], [49, 50, 0, 0, 0, 0, 51, -1, 52, 0, 0, 0, -1, -1, -1], [53, 0, 0, 0, -1, -1, 54, 55, 0, 0, 0, -1, 56, 57, 58], [59, 0, 0, -1, 60, 61, 0, 0, 0, 0, 0, 62, 0, 0, 0], [63, 0, 0, -1, 64, 0, 0, 0, 0, -1, 65, 0, 0, 0, 0], [66, 0, 0, -1, 67, 0, 0, 0, 0, -1, 68, 0, 0, 0, 0]]";

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Uri fileUri;

        public SectionsPagerAdapter(FragmentManager fm, Uri fileUri) {
            super(fm);
            this.fileUri = fileUri;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new CrosswordEditFragment( );
                default:
                    return PlaceholderFragment.newInstance(1);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }
}
