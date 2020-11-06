package com.example.mustapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mustapp.databinding.FragmentRegistrationBinding;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {
    private static final int CAMERA_CODE = 2;
    private ImageView imageView;
    private ProgressBar progressBar;
    private ValidationViewModel viewModel;
    private static final String PROGRESS_STATUS = "progressbar_status";
    private static final String ENCODED_IMAGE = "encodedImage";
    private String encodedImageString = "";

    public RegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentRegistrationBinding binding = FragmentRegistrationBinding.inflate(inflater, container
                , false);
        NavController controller = Navigation.findNavController(requireActivity(),
                R.id.navHostFragment);
        imageView = binding.profilePicView;
        progressBar = binding.progressRegister;
        if (savedInstanceState != null) {
            progressBar.setVisibility(savedInstanceState.getInt(PROGRESS_STATUS));
            encodedImageString = savedInstanceState.getString(ENCODED_IMAGE);
            if (encodedImageString != null && !encodedImageString.isEmpty())
                imageView.setImageBitmap(getDecodedBitmap(Base64.decode(encodedImageString,
                        Base64.DEFAULT)));
        }

        binding.setFragmentRegistration(this);
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (progressBar != null){ outState.putInt(PROGRESS_STATUS,progressBar.getVisibility());}
        outState.putString(ENCODED_IMAGE, encodedImageString);
        super.onSaveInstanceState(outState);
    }

    /**
     * Use the method to request a camera
     * @param view View clicked
     */
    public void startCamera(View view) {
        //create intent object to invoke the camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            Intent intent1 = Intent.createChooser(intent, "Choose Camera");
            startActivityForResult(intent1, CAMERA_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if successful
        if ((resultCode == RESULT_OK)) {
            if (requestCode == CAMERA_CODE) {
                if (data != null && data.getExtras() != null) {
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    if (bitmap != null) {
                        ByteArrayOutputStream bios = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bios);
                        Bitmap bitmap1 = getDecodedBitmap(bios.toByteArray());
                        encodedImageString = encodeBitmap(bitmap1);
                        imageView.setImageBitmap(bitmap1);
                    }
                }
            }
        }
    }

    /**
     * @param bitmap to be encoded
     * @return the encoded string
     */
    @SuppressWarnings("WeakerAccess")
    public @Nullable
    static String encodeBitmap(Bitmap bitmap) throws OutOfMemoryError {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes;
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        return "";
    }


    private static @Nullable Bitmap getDecodedBitmap(byte[] data) throws OutOfMemoryError {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//return null(bitmap) but allows fields to be queried prior to construction of bitmap;
        //check for dimension before decoding it i.e avoid #OutOfMemoryException
        //int height= options.outHeight;
        // int width = options.outWidth;
        // String mimeType = options.outMimeType;
        //load a scale down version into the memory
        BitmapFactory.decodeByteArray(data, 0, 0, options);
        //decode with inJustDecodeBounds= false
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, 200, 200);
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    @CheckResult
    private static int calculateInSampleSize(@NonNull BitmapFactory.Options options, int reqHeight, int reqWidth) {
        //raw height and width of the image
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            //calculate the largest inSampleSize that is pow2 and height and width are larger than the requested;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;

    }


    @InverseBindingAdapter(attribute = "selectedItem", event = "selectedItemAttrChanged")
    public static String captureSpinnerValueChange(Spinner spinner) {
        return (String) spinner.getSelectedItem();
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"selectedItem", "selectedItemAttrChanged"}, requireAll = false)
    public static void setOnItemSelectedListener(Spinner spinner, String selectedValue,
                                                 InverseBindingListener listener) {

        AdapterView.OnItemSelectedListener newListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner.setOnItemSelectedListener(newListener);

    }
}
