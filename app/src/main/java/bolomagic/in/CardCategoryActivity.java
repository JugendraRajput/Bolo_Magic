package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import bolomagic.in.AdaptorAndParse.CardCategoryParse;
import bolomagic.in.AdaptorAndParse.CardsCategoryAdapter;

public class CardCategoryActivity extends AppCompatActivity {

    ArrayList<CardCategoryParse> cardCategoryParses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_category);
        setTitle("Buy Gift Cards");

        final ListView listView = findViewById(R.id.categoryListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String category = cardCategoryParses.get(i).getCategoryName();
                Intent intent = new Intent(CardCategoryActivity.this,CardListActivity.class);
                intent.putExtra("Local Category",category);
                startActivity(intent);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Gift Cards");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Categories")){
                    Iterable<DataSnapshot> dataSnapshotIterable = snapshot.child("Categories").getChildren();
                    for (DataSnapshot next : dataSnapshotIterable) {
                        String categoryName = next.child("Name").getValue().toString();
                        String maxCashBack = next.child("Max CashBack").getValue().toString();
                        String imageURL = next.child("Image URL").getValue().toString();
                        int i = 0;
                        if (snapshot.hasChild("Products")){
                            Iterable<DataSnapshot> dataSnapshotIterable1 = snapshot.child("Products").getChildren();
                            for (DataSnapshot nextNew : dataSnapshotIterable1) {
                                if (nextNew.child("Category").getValue().toString().equals(categoryName));{
                                    i = i+1;
                                }
                            }
                        }
                        cardCategoryParses.add(new CardCategoryParse(categoryName,imageURL,"Upto "+maxCashBack+"% Cashback",i+" Gift Cards"));
                    }
                    CardsCategoryAdapter cardsAdapter = new CardsCategoryAdapter(CardCategoryActivity.this, cardCategoryParses);
                    listView.setAdapter(cardsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardCategoryActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gift_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                startActivity(new Intent(CardCategoryActivity.this,CartActivity.class));
                return true;
            case R.id.action_giftcardhistory:
                startActivity(new Intent(CardCategoryActivity.this,CardHistoryActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}