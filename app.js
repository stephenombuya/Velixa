document.addEventListener("DOMContentLoaded", () => {
    const pages = {
        register: document.getElementById("register-section"),
        login: document.getElementById("login-section"),
        browse: document.getElementById("browse-section"),
        cart: document.getElementById("cart-section"),
        payment: document.getElementById("payment-section"),
        paymentSuccess: document.getElementById("payment-success-section"),
    };

    const navButtons = {
        register: document.getElementById("nav-register-btn"),
        login: document.getElementById("nav-login-btn"),
        browse: document.getElementById("nav-browse-btn"),
        cart: document.getElementById("nav-cart-btn"),
        logout: document.getElementById("nav-logout-btn"),
    };

    let users = [];
    let currentUser = null;
    let cart = [];

    function showPage(page) {
        Object.values(pages).forEach((section) => (section.style.display = "none"));
        page.style.display = "block";
    }

    function updateCartCount() {
        const cartCount = document.getElementById("cart-count");
        cartCount.textContent = cart.length;
    }

    function renderEquipmentList() {
        const equipmentList = document.getElementById("equipment-list");
        equipmentList.innerHTML = "";

        const equipmentData = [
            { id: 1, name: "Yoga Mat", price: 20 },
            { id: 2, name: "Dumbbells", price: 50 },
            { id: 3, name: "Resistance Bands", price: 15 },
        ];

        equipmentData.forEach((item) => {
            const itemDiv = document.createElement("div");
            itemDiv.className = "equipment-item";

            itemDiv.innerHTML = `
                <h3>${item.name}</h3>
                <p class="price">$${item.price}</p>
                <button data-id="${item.id}" data-name="${item.name}" data-price="${item.price}">Add to Cart</button>
            `;

            equipmentList.appendChild(itemDiv);
        });

        equipmentList.querySelectorAll("button").forEach((btn) => {
            btn.addEventListener("click", () => {
                const { id, name, price } = btn.dataset;
                cart.push({ id, name, price: parseFloat(price) });
                updateCartCount();
            });
        });
    }

    function renderCartItems() {
        const cartItemsDiv = document.getElementById("cart-items");
        const totalPriceEl = document.getElementById("total-price");

        cartItemsDiv.innerHTML = "";
        let totalPrice = 0;

        cart.forEach((item, index) => {
            const cartItemDiv = document.createElement("div");
            cartItemDiv.className = "cart-item";

            cartItemDiv.innerHTML = `
                <span class="cart-item-name">${item.name}</span>
                <span class="cart-item-price">$${item.price.toFixed(2)}</span>
                <span class="cart-item-remove"><button data-index="${index}">Remove</button></span>
            `;

            cartItemsDiv.appendChild(cartItemDiv);
            totalPrice += item.price;
        });

        totalPriceEl.textContent = totalPrice.toFixed(2);

        cartItemsDiv.querySelectorAll("button").forEach((btn) => {
            btn.addEventListener("click", () => {
                const index = btn.dataset.index;
                cart.splice(index, 1);
                renderCartItems();
                updateCartCount();
            });
        });
    }

    navButtons.register.addEventListener("click", () => showPage(pages.register));
    navButtons.login.addEventListener("click", () => showPage(pages.login));

    navButtons.browse.addEventListener("click", () => {
        renderEquipmentList();
        showPage(pages.browse);
    });

    navButtons.cart.addEventListener("click", () => {
        renderCartItems();
        showPage(pages.cart);
    });

    navButtons.logout.addEventListener("click", () => {
        currentUser = null;
        cart = [];
        updateCartCount();
        Object.values(navButtons).forEach((btn) => (btn.style.display = ""));
        navButtons.browse.style.display = "none";
        navButtons.cart.style.display = "none";
        navButtons.logout.style.display = "none";
        showPage(pages.login);
    });

    document.getElementById("to-login-btn").addEventListener("click", () => showPage(pages.login));
    document.getElementById("to-register-btn").addEventListener("click", () => showPage(pages.register));

    document.getElementById("register-form").addEventListener("submit", (e) => {
        e.preventDefault();
        const username = document.getElementById("register-username").value;
        const password = document.getElementById("register-password").value;

        users.push({ username, password });
        alert("Registration successful! Please log in.");
        showPage(pages.login);
    });

    document.getElementById("login-form").addEventListener("submit", (e) => {
        e.preventDefault();
        const username = document.getElementById("login-username").value;
        const password = document.getElementById("login-password").value;

        const user = users.find((u) => u.username === username && u.password === password);

        if (user) {
            currentUser = user;
            navButtons.register.style.display = "none";
            navButtons.login.style.display = "none";
            navButtons.browse.style.display = "";
            navButtons.cart.style.display = "";
            navButtons.logout.style.display = "";
            showPage(pages.browse);
        } else {
            alert("Invalid username or password.");
        }
    });

    document.getElementById("checkout-btn").addEventListener("click", () => {
        showPage(pages.payment);
    });

    document.getElementById("payment-form").addEventListener("submit", (e) => {
        e.preventDefault();
        alert("Payment processed successfully!");
        cart = [];
        updateCartCount();
        showPage(pages.paymentSuccess);
    });

    document.getElementById("continue-shopping-btn").addEventListener("click", () => {
        showPage(pages.browse);
    });

    showPage(pages.register);
});
