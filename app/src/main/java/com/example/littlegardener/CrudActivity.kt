package com.example.littlegardener

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewStub
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smarteist.autoimageslider.SliderView


class CrudActivity : AppCompatActivity(), CrudProductAdapter.OnProductClickListener {
    private lateinit var type: String
    private lateinit var category: String
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var viewLayout: ConstraintLayout
    private lateinit var scrollLayout: ScrollView
    private lateinit var viewStub: ViewStub
    private lateinit var toolbar: Toolbar
    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var productAdapter: CrudProductAdapter
    private lateinit var productListRecyclerView: RecyclerView
    private var productList: MutableList<Product> = mutableListOf()
    private lateinit var productImagesAdapter : ProductImagesAdapter
    private lateinit var productImagesRecyclerView: RecyclerView
    private lateinit var currProduct: Product
    private var imagesList = mutableListOf<Uri>()
    private lateinit var sliderView: SliderView
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var productOption: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)
        type = intent.getStringExtra("type")!!
        if (type == "view_list") {
            category = intent.getStringExtra("category")!!
        }
        initUI()
    }

    private fun initUI() {
        rootLayout = findViewById(R.id.root_layout)
        viewStub = findViewById(R.id.view_stub)
        viewStub.layoutResource =
            when (type) {
                "add" -> R.layout.add_edit_product_layout
                else -> R.layout.list_product_layout
            }
        viewStub.inflate()
        when (type) {
            "add" -> loadAddProductDetails()
            else -> loadListProductDetails(type.split("_")[0])
        }
    }

    private fun setTitle() {
        toolbar = findViewById(R.id.toolbar)
        val title = type.replaceFirstChar { it.uppercase() }.split("_")[0] + " Item"
        toolbar.title = title
    }

    private fun customTitle(title: String) {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = title
    }

    private fun loadAddProductDetails() {
        setTitle()
        initUIFields()
        submitButton.setOnClickListener {
            if (validateAddFields()) {
                createNewProduct()
            }
        }
    }

    private fun initUIFields() {
        nameEditText = findViewById(R.id.product_name)
        priceEditText = findViewById(R.id.product_price)
        categoryEditText = findViewById(R.id.product_category)
        descriptionEditText = findViewById(R.id.description)
        submitButton = findViewById(R.id.submit_button)
        val addImage = findViewById<ImageView>(R.id.add_image_icon)
        addImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            attachImage.launch(intent)
        }
        productImagesRecyclerView = findViewById(R.id.images_recycler_view)
        productImagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        productImagesAdapter = ProductImagesAdapter(imagesList)
        productImagesRecyclerView.adapter = productImagesAdapter
    }

    private fun validateAddFields(): Boolean {
        val name = nameEditText.text.toString()
        val price = priceEditText.text.toString()
        val category = categoryEditText.text.toString()
        val description = descriptionEditText.text.toString()
        var valid = true
        if (name.isEmpty()) {
            nameEditText.error = "Name cannot be empty"
            valid = false
        }
        if (price.isEmpty()) {
            priceEditText.error = "Price cannot be empty"
            valid = false
        }
        if (category.isEmpty()) {
            categoryEditText.error = "Category cannot be empty"
            valid = false
        }
        if (description.isEmpty()) {
            descriptionEditText.error = "Description cannot be empty"
            valid = false
        }
        if (imagesList.isEmpty()) {
            Toast.makeText(this, "Please add at least one image", Toast.LENGTH_SHORT).show()
            valid = false
        }
        return valid
    }

    private fun createNewProduct() {
        val name = findViewById<EditText>(R.id.product_name).text.toString()
        val price = findViewById<EditText>(R.id.product_price).text.toString().toDouble()
        val category = findViewById<EditText>(R.id.product_category).text.toString()
        val description = findViewById<EditText>(R.id.description).text.toString()
        StorageHelper.uploadImages(this, imagesList) {
            val product = Product("", name, price, description, category, it, AuthenticationHelper.getAuth().currentUser!!.uid)
            FirestoreHelper.addProduct(product)
        }
        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show()
        finish()
    }

    private val attachImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data?.clipData
            if (data != null) {
                val count: Int = data.itemCount
                imagesList.clear()
                for (i in 0 until count) {
                    val imageUri: Uri = data.getItemAt(i).uri
                    imagesList.add(imageUri)
                }
                productImagesAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadEditProductDetails() {
        setTitle()
        initUIFields()
        updateProductFields()
        submitButton.setOnClickListener {
            if (validateAddFields()) {
                editProduct()
            }
        }
    }

    private fun updateProductFields() {
        nameEditText.setText(currProduct.name)
        priceEditText.setText(currProduct.price.toString())
        categoryEditText.setText(currProduct.category)
        categoryEditText.isEnabled = false
        descriptionEditText.setText(currProduct.description)
    }

    private fun editProduct() {
        val name = nameEditText.text.toString()
        val price = priceEditText.text.toString().toDouble()
        val category = categoryEditText.text.toString()
        val description = descriptionEditText.text.toString()
        StorageHelper.uploadImages(this, imagesList) {
            val product = Product(currProduct.id, name, price, description, category, it, AuthenticationHelper.getAuth().currentUser!!.uid)
            FirestoreHelper.updateProduct(product)
        }
        Toast.makeText(this, "Product edited", Toast.LENGTH_SHORT).show()
        finishEditLayout()
    }

    private fun loadListProductDetails(type: String) {
        productListRecyclerView = findViewById(R.id.product_recycler_view)
        productListRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = CrudProductAdapter(type, productList)
        productListRecyclerView.adapter = productAdapter
        if (this::category.isInitialized) {
            customTitle("${category.replaceFirstChar {it.uppercase()}} Products")
            loadCategoriesProductListener()
        } else {
            setTitle()
            loadProductListener()
        }
    }

    private fun loadCategoriesProductListener() {
        val collection = FirestoreHelper.getProductCollection()
        collection.whereEqualTo("category", category).addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            productList.clear()
            for (doc in value!!) {
                val product = doc.toObject(Product::class.java)
                product.id = doc.id
                productList.add(product)
            }
            productAdapter.notifyDataSetChanged()
        }
    }

    private fun loadProductListener() {
        val collection = FirestoreHelper.getProductCollection()
        collection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                productList.clear()
                for (document in snapshot) {
                    val product = Product(
                        document.id,
                        document.getString("name")!!,
                        document.getDouble("price")!!,
                        document.getString("description")!!,
                        document.getString("category")!!,
                        document.get("images") as List<String>,
                        document.getString("seller")!!
                    )
                    productList.add(product)
                }
                productAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadViewProductDetails() {
        sliderView = findViewById(R.id.slider)
        imageSliderAdapter = ImageSliderAdapter(currProduct.images as MutableList<String>)
        sliderView.setSliderAdapter(imageSliderAdapter, currProduct.images.size != 1)
        sliderView.setIndicatorVisibility(true)
        productOption = findViewById(R.id.order_options)
        findViewById<TextView>(R.id.product_title).text = currProduct.name
        val price = "RM%.2f".format(currProduct.price)
        findViewById<TextView>(R.id.product_price).text = price
        findViewById<TextView>(R.id.product_category).text = currProduct.category
        findViewById<TextView>(R.id.product_description).text = currProduct.description
        productOption.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.chat_icon -> {
                    val intent = Intent(this, LiveChatActivity::class.java)
                    intent.putExtra("type", "new")
                    intent.putExtra("chatItem", ChatItem(receiver = currProduct.seller))
                    startActivity(intent)
                    true
                }
                R.id.cart_icon -> {
                    FirestoreHelper.addProductToCart(currProduct)
                    Toast.makeText(this, "Product added to cart", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    //Order Activity
                    true
                }
            }
        }
    }

    override fun onProductEditClick(product: Product) {
        rootLayout.removeAllViews()
        scrollLayout = LayoutInflater.from(this).inflate(R.layout.add_edit_product_layout, rootLayout, false) as ScrollView
        rootLayout.addView(scrollLayout)
        currProduct = product
        type = "edit"
        val uriList: MutableList<Uri> = mutableListOf()
        product.images.forEach{ s -> uriList.add(Uri.parse(s)) }
        imagesList = uriList
        loadEditProductDetails()
    }

    override fun onProductShowClick(product: Product) {
        rootLayout.removeAllViews()
        viewLayout = LayoutInflater.from(this).inflate(R.layout.view_product_layout, rootLayout, false) as ConstraintLayout
        rootLayout.addView(viewLayout)
        currProduct = product
        type = "view"
        loadViewProductDetails()
    }

    private fun finishEditLayout() {
        type = "edit_list"
        rootLayout.removeAllViews()
        viewLayout = LayoutInflater.from(this).inflate(R.layout.list_product_layout, rootLayout, false) as ConstraintLayout
        rootLayout.addView(viewLayout)
        loadListProductDetails("edit")
    }

    private fun finishViewLayout() {
        type = "view_list"
        rootLayout.removeAllViews()
        viewLayout = LayoutInflater.from(this).inflate(R.layout.list_product_layout, rootLayout, false) as ConstraintLayout
        rootLayout.addView(viewLayout)
        loadListProductDetails("view")
    }

    override fun onBackPressed() {
        when (type) {
            "edit" -> {
                finishEditLayout()
            }
            "view" -> {
                finishViewLayout()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}