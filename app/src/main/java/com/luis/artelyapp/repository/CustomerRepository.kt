package com.luis.artelyapp.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.luis.artelyapp.model.Customer
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para operaciones con Firebase Realtime Database
 * relacionadas con clientes (Customers)
 */
class CustomerRepository {

    private val database = FirebaseDatabase.getInstance()
    private val customersRef = database.getReference("customers")

    companion object {
        private const val TAG = "CustomerRepository"
    }

    /**
     * Obtiene un cliente por su ID desde Firebase
     */
    suspend fun getCustomerById(customerId: String): Customer? {
        return try {
            val snapshot = customersRef.child(customerId).get().await()
            snapshot.getValue(Customer::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener cliente $customerId: ${e.message}")
            null
        }
    }

    /**
     * Obtiene un cliente por su UID de Firebase
     */
    suspend fun getCustomerByUID(uid: String): Customer? {
        return try {
            val snapshot = customersRef.child(uid).get().await()
            snapshot.getValue(Customer::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener cliente por UID $uid: ${e.message}")
            null
        }
    }

    /**
     * Obtiene un cliente por su nombre de usuario
     */
    suspend fun getCustomerByUserName(userName: String): Customer? {
        return try {
            val snapshot = customersRef
                .orderByChild("userName")
                .equalTo(userName)
                .limitToFirst(1)
                .get()
                .await()

            snapshot.children.firstOrNull()?.getValue(Customer::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener cliente por nombre: ${e.message}")
            null
        }
    }

    /**
     * Busca un cliente por email
     */
    suspend fun getCustomerByEmail(email: String): Customer? {
        return try {
            val snapshot = customersRef
                .orderByChild("email")
                .equalTo(email)
                .limitToFirst(1)
                .get()
                .await()

            snapshot.children.firstOrNull()?.getValue(Customer::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener cliente por email $email: ${e.message}")
            null
        }
    }

    /**
     * Guarda o actualiza un cliente en Firebase
     */
    suspend fun saveCustomer(customer: Customer): Boolean {
        return try {
            customersRef.child(customer.id_User.toString()).setValue(customer).await()
            Log.d(TAG, "Cliente ${customer.id_User} guardado correctamente")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar cliente: ${e.message}")
            false
        }
    }

    /**
     * Obtiene todos los clientes desde Firebase
     */
    suspend fun getAllCustomers(): Result<List<Customer>> {
        return try {
            val snapshot = customersRef.get().await()
            val customers = snapshot.children.mapNotNull {
                it.getValue(Customer::class.java)
            }

            Log.d(TAG, "Se obtuvieron ${customers.size} clientes")
            Result.success(customers)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener clientes: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Elimina un cliente de Firebase
     */
    suspend fun deleteCustomer(customerId: String): Result<Boolean> {
        return try {
            customersRef.child(customerId).removeValue().await()
            Log.d(TAG, "Cliente $customerId eliminado correctamente")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar cliente: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Método de debug: Lista todos los customers en Firebase para debugging
     */
    suspend fun debugListAllCustomers(): Map<String, Customer> {
        return try {
            val snapshot = customersRef.get().await()
            val customers = mutableMapOf<String, Customer>()

            for (child in snapshot.children) {
                val customer = child.getValue(Customer::class.java)
                if (customer != null) {
                    customers[child.key ?: "unknown"] = customer
                    Log.d(TAG, "🐛 DEBUG - Customer encontrado: key=${child.key}, name=${customer.UserName}, email=${customer.Email}")
                }
            }

            Log.d(TAG, "🐛 DEBUG - Total customers encontrados: ${customers.size}")
            customers
        } catch (e: Exception) {
            Log.e(TAG, "Error al listar customers: ${e.message}")
            emptyMap()
        }
    }
}

