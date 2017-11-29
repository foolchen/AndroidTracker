package com.foolchen.lib.tracker.demo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.foolchen.lib.tracker.demo.R;

/**
 * 验证ButterKnife点击的Fragment
 *
 * @author chenchong
 *         2017/11/29
 *         下午2:33
 */

public class ButterKnifeFragment extends BaseFragment {

  @BindView(R.id.rv) RecyclerView mRv;
  private Unbinder mUnbinder;

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_butter_knife, container, false);
    mUnbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRv.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  @Override public void onDestroyView() {
    mUnbinder.unbind();
    super.onDestroyView();
  }

  @OnClick(R.id.btn_apply_data) public void apply() {
    mRv.setAdapter(new ButterKnifeAdapter());
  }

  static class ButterKnifeAdapter extends RecyclerView.Adapter<ButterKnifeHolder> {

    @Override public ButterKnifeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new ButterKnifeHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_butter_knife, parent, false));
    }

    @Override public void onBindViewHolder(ButterKnifeHolder holder, int position) {
      holder.mTvClickable.setText(holder.itemView.getContext()
          .getString(R.string.text_clickable_mask, String.valueOf(position)));
      holder.mTvNotClickable.setText(holder.itemView.getContext()
          .getString(R.string.text_not_clickable_mask, String.valueOf(position)));
    }

    @Override public int getItemCount() {
      return 100;
    }
  }

  static class ButterKnifeHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_clickable) TextView mTvClickable;
    @BindView(R.id.tv_not_clickable) TextView mTvNotClickable;

    public ButterKnifeHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Toast.makeText(v.getContext(), "ItemView " + getAdapterPosition(), Toast.LENGTH_SHORT)
              .show();
        }
      });
    }

    @OnClick(R.id.tv_clickable) public void click(View view) {
      Toast.makeText(view.getContext(), ((TextView) view).getText().toString(), Toast.LENGTH_SHORT)
          .show();
    }
  }
}
