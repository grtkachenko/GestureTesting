package com.grtkachenko.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private CameraBridgeViewBase openCvCameraView;
        private Mat intermediateMat;
        private BaseLoaderCallback loaderCallback;
        private BackgroundSubtractorMOG backgroundSubtractor;

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
                    backgroundSubtractor = new BackgroundSubtractorMOG();
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
                    Mat rgba = inputFrame.rgba();
                    Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
                    backgroundSubtractor.apply(rgba, rgba);
                    return rgba;
                }

                private Mat rotate(Mat mat) {
                    Mat mRgbaT = mat.t();
                    Core.flip(mat.t(), mRgbaT, 1);
                    Imgproc.resize(mRgbaT, mRgbaT, mat.size());
                    return mRgbaT;
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
