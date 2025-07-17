import com.example.sportsequipmentstore.model.AddressModel

interface AddressRepository {
    fun addAddress(address: AddressModel, callback: (Boolean, String) -> Unit)
    fun getAllAddresses(callback: (List<AddressModel>) -> Unit)
    fun deleteAddress(addressId: String, callback: (Boolean, String) -> Unit)
}
