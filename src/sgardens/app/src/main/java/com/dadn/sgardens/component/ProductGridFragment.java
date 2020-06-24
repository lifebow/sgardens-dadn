package com.dadn.sgardens.component;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dadn.sgardens.DetailActivity;
import com.dadn.sgardens.ProductCardRecyclerViewAdapter;
import com.dadn.sgardens.ProductGridItemDecoration;
import com.dadn.sgardens.R;
import com.dadn.sgardens.network.ProductEntry;

public class ProductGridFragment extends Fragment implements ProductCardRecyclerViewAdapter.OnItemClickListener {
    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_TITLE = "TitleName";
    public static final String EXTRA_SUBTITLE = "Subtitle";


    private ProductCardRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.product_grid_fragment, container, false);

        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));
        adapter = new ProductCardRecyclerViewAdapter(
                ProductEntry.initProductEntryList(getResources()),getActivity());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.product_grid_spacing_small);
        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getActivity().getBaseContext(), DetailActivity.class);
        ProductEntry product = adapter.getProductList().get(position);

        detailIntent.putExtra(EXTRA_URL, product.url);
        detailIntent.putExtra(EXTRA_TITLE, product.title);
        detailIntent.putExtra(EXTRA_SUBTITLE, product.price);

        startActivity(detailIntent);
    }
}
