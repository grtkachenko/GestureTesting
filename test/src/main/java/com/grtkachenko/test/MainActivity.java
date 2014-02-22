package com.grtkachenko.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends ActionBarActivity {
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        seekBar = (SeekBar) findViewById(R.id.seekBar);
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public static class PlaceholderFragment extends Fragment {

        private CameraBridgeViewBase openCvCameraView;
        private Mat intermediateMat;
        private BaseLoaderCallback loaderCallback;

        private final int ratio = 3;
        private final int kernelSize = 3;

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            openCvCameraView = (CameraBridgeViewBase) rootView.findViewById(R.id.image_manipulations_activity_surface_view);
            openCvCameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
                @Override
                public void onCameraViewStarted(int width, int height) {
                    intermediateMat = new Mat();
                }

                @Override
                public void onCameraViewStopped() {
                    if (intermediateMat != null) {
                        intermediateMat.release();
                    }

                    intermediateMat = null;
                }

                @Override
                public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                    int lowThreshold = ((MainActivity) getActivity()).getSeekBar().getProgress();
                    Mat rgba = inputFrame.rgba();
                    Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
                    Imgproc.blur(rgba, rgba, new Size(3, 3));
                    Imgproc.Canny(rgba, rgba, lowThreshold, lowThreshold * ratio, kernelSize, false);
                    return rgba;
                }

            });
            loaderCallback = new BaseLoaderCallback(getActivity().getApplicationContext()) {
                @Override
                public void onManagerConnected(int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:
                        {
                            openCvCameraView.enableView();
                        } break;
                        default:
                        {
                            super.onManagerConnected(status);
                        } break;
                    }
                }
            };
            openCvCameraView.setMaxFrameSize(400, 400);
            return rootView;
        }

        @Override
        public void onPause() {
            super.onPause();
            if (openCvCameraView != null) {
                openCvCameraView.disableView();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, getActivity().getApplicationContext(), loaderCallback);
        }

        public void onDestroy() {
            super.onDestroy();
            if (openCvCameraView != null) {
                openCvCameraView.disableView();
            }
        }

    }
}
