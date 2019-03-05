package com.letsride.passenger.mBox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import com.letsride.passenger.R;
import com.letsride.passenger.utils.Log;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.letsride.passenger.mBox.BoxOrder.ADDRESS_KEY;
import static com.letsride.passenger.mBox.BoxOrder.DESTINATION_LOCATION_REQUEST_CODE;
import static com.letsride.passenger.mBox.BoxOrder.LAT_KEY;
import static com.letsride.passenger.mBox.BoxOrder.LONG_KEY;
import static com.letsride.passenger.mBox.BoxOrder.POSITION;

/**
 * Created by Androgo on 12/16/2018.
 */

public class DestinasiItem extends AbstractItem<DestinasiItem, DestinasiItem.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    private String location;
    private double lat;
    private double lng;
    private String detailLocation;
    private String namaReceiver;
    private String teleponReceiver;
    private String instruction;
    private Context mContext;
    private ViewHolder holderFinal;

    public DestinasiItem(Context context) {
        mContext = context;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDetailLocation() {
        return detailLocation;
    }

    public void setDetailLocation(String detailLocation) {
        this.detailLocation = detailLocation;
    }

    public String getNamaReceiver() {
        return namaReceiver;
    }

    public void setNamaReceiver(String namaReceiver) {
        this.namaReceiver = namaReceiver;
    }

    public String getTeleponReceiver() {
        return teleponReceiver;
    }

    public void setTeleponReceiver(String teleponReceiver) {
        this.teleponReceiver = teleponReceiver;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    @Override
    public int getType() {
        return R.id.next_destination;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_destinasi;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);


        holderFinal = holder;
        final int position = holder.getLayoutPosition();
//        Log.e("POSITION", position+"");

        holder.destinationLocation.setFocusable(false);
        holder.destinationLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity mbox = (Activity) mContext;
                Intent intent = new Intent(mContext, PickLocation.class);
                intent.putExtra(POSITION, position);
                mbox.startActivityForResult(intent, DESTINATION_LOCATION_REQUEST_CODE);
            }
        });

        holder.destinationLocDetail.setText(detailLocation);
        holder.destinationLocDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                detailLocation = holderFinal.destinationLocDetail.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.destinationContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.addContact.getVisibility() == View.VISIBLE) {
                    holder.addContact.setVisibility(View.GONE);
                } else {
                    holder.addContact.setVisibility(View.VISIBLE);
                }

                holder.destinationName.setText(namaReceiver);
                holder.destinationName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        namaReceiver = holderFinal.destinationName.getText().toString();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                holder.destinationPhone.setText(teleponReceiver);
                holder.destinationPhone.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        teleponReceiver = holderFinal.destinationPhone.getText().toString();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });

        holder.destinationInstruction.setText(instruction);
        holder.destinationInstruction.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                instruction = holderFinal.destinationInstruction.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        holder.buttonRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("DESTINATION", "REMOVE");
//            }
//        });

    }

    public void setLocationData(Intent data) {
        holderFinal.destinationLocation.setText(data.getStringExtra(BoxOrder.ADDRESS_KEY));
        location = data.getStringExtra(BoxOrder.ADDRESS_KEY);
        lat = data.getDoubleExtra(LAT_KEY, 0);
        lng = data.getDoubleExtra(LONG_KEY, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
        String address = data.getStringExtra(ADDRESS_KEY);
        View view = generateView(mContext);
        getViewHolder(view).destinationLocation.setText(address);
//        get
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.destinationLocDetail.setText(null);
        holder.destinationName.setText(null);
        holder.destinationPhone.setText(null);
        holder.destinationInstruction.setText(null);
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @NotEmpty
        @BindView(R.id.dest_loc)
        EditText destinationLocation;

        @NotEmpty
        @BindView(R.id.dest_locdetail)
        EditText destinationLocDetail;

        @BindView(R.id.dest_contact)
        TextView destinationContact;

        @BindView(R.id.add_destcontact)
        LinearLayout addContact;

        @NotEmpty
        @BindView(R.id.dest_contactname)
        EditText destinationName;

        @NotEmpty
        @BindView(R.id.dest_contactphone)
        EditText destinationPhone;

        @BindView(R.id.dest_instruction)
        EditText destinationInstruction;

//        @BindView(R.id.btn_removenext)
//        Button buttonRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }
}
