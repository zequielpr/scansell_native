package com.kunano.scansell_native.ui.home.business.create_product.bottom_sheet_image_source;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.databinding.FragmentImageSourceBinding;


public class ImageSourceFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentImageSourceBinding binding;
    private ImageButton imageButtonCamera;
    private ImageButton imageButtonFiles;
    private imageSoucerLisner imageSoucerLisner;

    public ImageSourceFragment() {
        // Required empty public constructor
    }

   /* *
    public static ImageSourceFragment newInstance(String param1, String param2) {
        ImageSourceFragment fragment = new ImageSourceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageSourceBinding.inflate(inflater, container, false);

        imageButtonFiles = binding.imageButtonFiles;
        imageButtonCamera = binding.imageButtonCamera;


        imageButtonFiles.setOnClickListener(imageSoucerLisner::fromFiles);
        imageButtonCamera.setOnClickListener(imageSoucerLisner::fromCamera);




        return binding.getRoot();
    }


    public void setImageSoucerLisner(ImageSourceFragment.imageSoucerLisner imageSoucerLisner) {
        this.imageSoucerLisner = imageSoucerLisner;
    }

    public interface imageSoucerLisner{
        abstract void fromFiles(View view);
        abstract void fromCamera(View view);
    }
}