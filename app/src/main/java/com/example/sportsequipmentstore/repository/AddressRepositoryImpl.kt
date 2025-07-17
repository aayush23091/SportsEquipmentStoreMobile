import com.example.sportsequipmentstore.model.AddressModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddressRepositoryImpl : AddressRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun addAddress(address: AddressModel, callback: (Boolean, String) -> Unit) {
        val docRef = db.collection("users").document(userId)
            .collection("addresses").document()
        val newAddress = address.copy(id = docRef.id)
        docRef.set(newAddress)
            .addOnSuccessListener { callback(true, "Address saved") }
            .addOnFailureListener { callback(false, it.message ?: "Error") }
    }

    override fun getAllAddresses(callback: (List<AddressModel>) -> Unit) {
        db.collection("users").document(userId)
            .collection("addresses").get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { it.toObject(AddressModel::class.java) }
                callback(list)
            }
    }

    override fun deleteAddress(addressId: String, callback: (Boolean, String) -> Unit) {
        db.collection("users").document(userId)
            .collection("addresses").document(addressId)
            .delete()
            .addOnSuccessListener { callback(true, "Deleted") }
            .addOnFailureListener { callback(false, it.message ?: "Error") }
    }
}
