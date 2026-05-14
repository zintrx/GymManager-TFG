document.addEventListener('DOMContentLoaded', () => {
    // 1. Session Check
    const userData = JSON.parse(localStorage.getItem('gym_user'));
    if (!userData) {
        window.location.href = 'index.html';
        return;
    }

    if (userData.role !== 'ADMIN') {
        // Hiding admin elements, showing client ones
        document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'none');
        document.querySelectorAll('.client-only').forEach(el => el.style.display = 'block');
        
        const nameElem = document.getElementById('adminName');
        if (nameElem) nameElem.innerText = userData.username;
        
        // Load only my payments
        if (userData.clienteId) {
            loadClientPayments(userData.clienteId);
        }
        
        // NEW: Load shop history for client
        if (userData.id) {
            loadClientShopHistory(userData.id);
        }
        
    } else {
        document.querySelectorAll('.client-only').forEach(el => el.style.display = 'none');
        document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'block');

        // 2. Load Clients for Select (Admin only)
        loadClientsForSelect();
        
        // 3. Load Payment History (Admin only)
        loadPaymentHistory();

        // NEW: Load All Shop Sales (Admin only)
        loadShopHistory();
    }

    // 4. Handle Payment Submission (Only if form exists)
    const paymentForm = document.getElementById('paymentForm');
    if (paymentForm) {
        paymentForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const paymentData = {
                cliente: { id: document.getElementById('selectCliente').value },
                monto: document.getElementById('monto').value,
                concepto: document.getElementById('concepto').value,
                metodoPago: document.getElementById('metodoPago').value
            };

            try {
                const response = await fetch('http://localhost:8081/api/pagos', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(paymentData)
                });

                if (response.ok) {
                    alert('Pago registrado correctamente');
                    document.getElementById('paymentForm').reset();
                    loadPaymentHistory();
                } else {
                    alert('Error al registrar el pago');
                }
            } catch (error) {
                console.error('Error:', error);
            }
        });
    }

    // Logout
    document.getElementById('btnLogout').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('gym_user');
        window.location.href = 'index.html';
    });
});

async function loadClientsForSelect() {
    const select = document.getElementById('selectCliente');
    try {
        const response = await fetch('http://localhost:8081/api/clientes');
        const clients = await response.json();
        
        select.innerHTML = '<option value="">Selecciona un cliente...</option>';
        clients.forEach(client => {
            const option = document.createElement('option');
            option.value = client.id;
            option.textContent = `${client.nombre} ${client.apellidos} (${client.dni})`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading clients:', error);
    }
}

async function loadPaymentHistory() {
    const tableBody = document.getElementById('paymentsTableBody');
    if (!tableBody) return;
    
    tableBody.innerHTML = '<tr><td colspan="4">Cargando pagos...</td></tr>';
    
    try {
        const response = await fetch('http://localhost:8081/api/pagos');
        const payments = await response.json();
        
        tableBody.innerHTML = '';
        
        if (payments.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="4">No hay pagos registrados. Registra uno a la izquierda.</td></tr>';
            return;
        }

        // Show last 10, newest first
        payments.slice().reverse().slice(0, 10).forEach(payment => {
            const row = document.createElement('tr');
            const fecha = new Date(payment.fechaPago).toLocaleDateString();
            row.innerHTML = `
                <td>${payment.cliente.nombre} ${payment.cliente.apellidos}</td>
                <td>${payment.monto.toFixed(2)}€</td>
                <td>${fecha}</td>
                <td><span class="badge badge-success">${payment.metodoPago || 'Efectivo'}</span></td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading payments:', error);
        tableBody.innerHTML = '<tr><td colspan="4" style="color:red;">Error al cargar historial</td></tr>';
    }
}

async function loadClientPayments(clienteId) {
    const tableBody = document.getElementById('clientPaymentsBody');
    if (!tableBody) return;
    
    try {
        const response = await fetch(`http://localhost:8081/api/pagos/cliente/${clienteId}`);
        const payments = await response.json();
        
        tableBody.innerHTML = '';
        
        if (payments.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="4" style="text-align: center;">Aún no has realizado ningún pago.</td></tr>';
            return;
        }

        payments.slice().reverse().forEach(payment => {
            const row = document.createElement('tr');
            const fecha = new Date(payment.fechaPago).toLocaleDateString();
            row.innerHTML = `
                <td>${fecha}</td>
                <td>${payment.concepto}</td>
                <td>${payment.monto.toFixed(2)}€</td>
                <td><span class="badge badge-success">${payment.metodoPago || 'Efectivo'}</span></td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading my payments:', error);
    }
}

// NEW: Load shop history for client
async function loadClientShopHistory(usuarioId) {
    const tableBody = document.getElementById('clientShopBody');
    if (!tableBody) return;

    try {
        const response = await fetch(`http://localhost:8081/api/compras/usuario/${usuarioId}`);
        const compras = await response.json();

        tableBody.innerHTML = '';

        if (compras.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="3" style="text-align: center;">No hay compras registradas en la tienda.</td></tr>';
            return;
        }

        compras.forEach(compra => {
            const row = document.createElement('tr');
            const fecha = new Date(compra.fecha).toLocaleString('es-ES', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' });
            row.innerHTML = `
                <td>${fecha}</td>
                <td>${compra.producto}</td>
                <td>${compra.precio.toFixed(2)}€</td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading client shop history:', error);
    }
}

// NEW: Load all shop history for Admin
async function loadShopHistory() {
    const tableBody = document.getElementById('adminShopBody');
    const totalElem = document.getElementById('totalVentasStore');
    if (!tableBody) return;

    try {
        const response = await fetch('http://localhost:8081/api/compras');
        const compras = await response.json();

        tableBody.innerHTML = '';
        let total = 0;

        if (compras.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="4" style="text-align: center;">No hay ventas registradas.</td></tr>';
            if (totalElem) totalElem.innerText = 'Total: 0.00€';
            return;
        }

        compras.forEach(compra => {
            total += compra.precio;
            const row = document.createElement('tr');
            const fecha = new Date(compra.fecha).toLocaleString('es-ES', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' });
            row.innerHTML = `
                <td>${fecha}</td>
                <td>${compra.usuario.username}</td>
                <td>${compra.producto}</td>
                <td>${compra.precio.toFixed(2)}€</td>
            `;
            tableBody.appendChild(row);
        });

        if (totalElem) totalElem.innerText = `Total: ${total.toFixed(2)}€`;
    } catch (error) {
        console.error('Error loading admin shop history:', error);
    }
}

