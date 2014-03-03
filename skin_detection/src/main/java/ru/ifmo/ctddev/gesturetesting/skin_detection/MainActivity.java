package ru.ifmo.ctddev.gesturetesting.skin_detection;
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
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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
                    Mat rgba = inputFrame.rgba();
                    Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2RGB);
                    Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2HSV);
                    Core.inRange(rgba, new Scalar(0, 100, 30), new Scalar(5, 255, 255), rgba);
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
