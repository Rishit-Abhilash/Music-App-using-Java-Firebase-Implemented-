package utils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("songs");
    }
}
