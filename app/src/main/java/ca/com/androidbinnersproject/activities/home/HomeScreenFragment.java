
package ca.com.androidbinnersproject.activities.home;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ca.com.androidbinnersproject.R;
import ca.com.androidbinnersproject.activities.pickup.PickupActivity;

public class HomeScreenFragment extends Fragment implements OnMapReadyCallback {
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mMapView;
	private MarkerOptions markerOptions;
	private LatLng latLng;

	private SearchView edtSearch;
	private Toolbar mToolbarBottom;

	public HomeScreenFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home_screen, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		mToolbarBottom = (Toolbar) view.findViewById(R.id.home_screen_include_toolbar);
		edtSearch      = (SearchView) view.findViewById(R.id.fragment_home_screen_edtSearch);

		setToolbatClickListener();

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		// Assumes current activity is the searchable activity
		edtSearch.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
		edtSearch.setIconifiedByDefault(false);
		edtSearch.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					onSearch(edtSearch.toString());
				}

				return false;
			}
		});


		super.onViewCreated(view, savedInstanceState);
	}

	private void setToolbatClickListener() {
		mToolbarBottom.findViewById(R.id.toolbar_bottom_btnHistory).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		mToolbarBottom.findViewById(R.id.toolbar_bottom_btnOngoing).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		mToolbarBottom.findViewById(R.id.toolbar_bottom_btnPickup).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), PickupActivity.class);
				startActivity(intent);
			}
		});

		mToolbarBottom.findViewById(R.id.toolbar_bottom_btnNotifications).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		mToolbarBottom.findViewById(R.id.toolbar_bottom_btnDonate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	@Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        mSupportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.fragment_home_screen_map_container);

        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.fragment_home_screen_map_container, mSupportMapFragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mSupportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMapView.setMyLocationEnabled(true);

            showCurrentLocation();
        } else {
            // Show rationale and request permission.
        }
    }

    private void showCurrentLocation() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null) {
            // Creating a LatLng object for the current location
            LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());

            mMapView.clear();

            mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15));

            mMapView.addMarker(new MarkerOptions().position(myPosition).title(getActivity().getResources().getString(R.string.home_screen_map_marker_title))).showInfoWindow();
        }
    }

	private void onSearch(final String search) {
		Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

		try {
			List<Address> addresses = geocoder.getFromLocationName(search, 5);

			if(addresses==null || addresses.size()==0){
				Toast.makeText(getActivity(), "No Location found", Toast.LENGTH_SHORT).show();
			}

			// Clears all the existing markers on the map
			mMapView.clear();

			// Adding Markers on Google Map for each matching address
			for(int i=0;i<addresses.size();i++){

				Address address = (Address) addresses.get(i);

				// Creating an instance of GeoPoint, to display in Google Map
				latLng = new LatLng(address.getLatitude(), address.getLongitude());

				String addressText = String.format("%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
						address.getCountryName());

				markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title(addressText);

				mMapView.addMarker(markerOptions);

				// Locate the first location
				if(i==0)
					mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
			}

		} catch (IOException e) {
			Log.e("MAPSERROR", "");
		}
	}
}
