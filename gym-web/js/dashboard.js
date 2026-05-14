document.addEventListener('DOMContentLoaded', () => {
    // 1. Session Check
    const userData = JSON.parse(localStorage.getItem('gym_user'));
    if (!userData) {
        window.location.href = 'index.html';
        return;
    }

    if (userData.role !== 'ADMIN') {
        document.querySelectorAll('.admin-only').forEach(el => {
            el.style.display = 'none';
        });
        document.querySelectorAll('.client-only').forEach(el => {
            el.style.display = 'block';
        });
        const clientNameHeader = document.getElementById('clientNameHeader');
        if (clientNameHeader) clientNameHeader.innerText = userData.username.toUpperCase();

        // Fetch Real Client Data for Dashboard
        if (userData.clienteId) {
            loadClientDashboardData(userData.clienteId);
        }
    } else {
        document.querySelectorAll('.client-only').forEach(el => {
            el.style.display = 'none';
        });
        document.querySelectorAll('.admin-only').forEach(el => {
            el.style.display = 'block';
        });
    }

    // 2. Set Admin Info
    const adminNameElem = document.getElementById('adminName');
    const adminAvatarElem = document.getElementById('adminAvatar');
    if (adminNameElem) adminNameElem.innerText = userData.username;
    if (adminAvatarElem) adminAvatarElem.innerText = userData.username.charAt(0).toUpperCase();

    if (userData.role === 'ADMIN') {
        loadStats();
        loadRecentClients();
        loadRecentPurchases();
        initAttendanceChart();
    }

    // 4. Logout
    document.getElementById('btnLogout').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('gym_user');
        window.location.href = 'index.html';
    });

    // 5. Navigation (Removed interceptor to ensure links work)

    // 6. Report generation
    const btnGenerateReport = document.getElementById('btnGenerateReport');
    if (btnGenerateReport) {
        btnGenerateReport.addEventListener('click', async () => {
        try {
            const response = await fetch('http://localhost:8081/api/reports/clientes');
            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'informe_clientes.pdf';
                document.body.appendChild(a);
                a.click();
                a.remove();
            } else {
                alert('No se pudo generar el reporte. Verifica que el servidor tenga el archivo .jrxml');
            }
        } catch (error) {
            console.error('Error generating report:', error);
            alert('Error de conexión con el servidor');
        }
    });
    }

    // 7. QR Modal logic
    const qrModal = document.getElementById('qrModal');
    const btnShowQR = document.getElementById('btnShowQR');
    const closeQrBtn = document.getElementById('closeQrModal');

    if (btnShowQR) {
        btnShowQR.onclick = () => {
            qrModal.style.display = 'flex';
        };
    }
    if (closeQrBtn) closeQrBtn.onclick = () => qrModal.style.display = 'none';

    // 8. Store Modal logic
    const storeModal = document.getElementById('storeModal');
    const btnShowStore = document.getElementById('btnShowStore');
    const closeStoreBtn = document.getElementById('closeStoreModal');

    if (btnShowStore) {
        btnShowStore.onclick = () => {
            storeModal.style.display = 'flex';
        };
    }
    const btnShowStoreClient = document.getElementById('btnShowStoreClient');
    if (btnShowStoreClient) {
        btnShowStoreClient.onclick = () => {
            storeModal.style.display = 'flex';
        };
    }
    if (closeStoreBtn) closeStoreBtn.onclick = () => storeModal.style.display = 'none';

    // 9. Profile Logic
    const btnShowProfile = document.getElementById('btnShowProfile');
    const btnShowProfileManual = document.getElementById('btnShowProfileManual');
    
    if (btnShowProfile) btnShowProfile.onclick = () => window.location.href = 'profile.html';
    if (btnShowProfileManual) btnShowProfileManual.onclick = () => window.location.href = 'profile.html';
    
    const btnShowProfileManualClient = document.getElementById('btnShowProfileManualClient');
    if (btnShowProfileManualClient) btnShowProfileManualClient.onclick = () => window.location.href = 'profile.html';

    const btnShowQRClient = document.getElementById('btnShowQRClient');
    if (btnShowQRClient) btnShowQRClient.onclick = () => { qrModal.style.display = 'flex'; };

    window.onclick = (e) => { 
        if (e.target == qrModal) qrModal.style.display = 'none';
        if (e.target == storeModal) storeModal.style.display = 'none';
    };

    // 10. Calendar Logic (If on calendar page)
    const actModal = document.getElementById('activityModal');
    const btnAddAct = document.getElementById('btnAddActivity');
    const closeAct = document.getElementById('closeActivityModal');

    if (btnAddAct) btnAddAct.onclick = () => actModal.style.display = 'flex';
    if (closeAct) closeAct.onclick = () => actModal.style.display = 'none';

    if (document.getElementById('addActivityForm')) {
        document.getElementById('addActivityForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = {
                titulo: document.getElementById('actTitle').value,
                fechaHora: `2026-04-11T${document.getElementById('actTime').value}:00`,
                sala: document.getElementById('actSala').value
            };
            try {
                const resp = await fetch('http://localhost:8081/api/actividades', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });
                if (resp.ok) {
                    actModal.style.display = 'none';
                    loadActivities();
                }
            } catch (err) { console.error(err); }
        });
    }

    if (document.getElementById('clientForm')) {
        document.getElementById('clientForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const id = document.getElementById('editClientId').value;
            const data = {
                nombre: document.getElementById('newNombre').value,
                apellidos: document.getElementById('newApellidos').value,
                dni: document.getElementById('newDni').value,
                email: document.getElementById('newEmail').value,
                telefono: document.getElementById('newTelefono').value,
                estado: document.getElementById('newEstado').value
            };
            const url = id ? `http://localhost:8081/api/clientes/${id}` : 'http://localhost:8081/api/clientes';
            const method = id ? 'PUT' : 'POST';
            try {
                const resp = await fetch(url, {
                    method: method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });
                if (resp.ok) {
                    document.getElementById('clientModal').style.display = 'none';
                    loadStats();
                    loadRecentClients();
                }
            } catch (err) { console.error(err); }
        });
    }

});

async function loadActivities() {
    const list = document.querySelector('.calendar-container div[style*="margin-top: 2rem"]');
    try {
        const resp = await fetch('http://localhost:8081/api/actividades');
        if (resp.ok) {
            const activities = await resp.json();
            const container = list.querySelector('div:last-child');
            if (container && activities.length > 0) {
                // Simplified render
                activities.forEach(a => {
                    const card = document.createElement('div');
                    card.className = 'stat-card';
                    card.style.marginTop = '1rem';
                    card.style.borderLeft = '4px solid var(--primary)';
                    card.innerHTML = `<p style="font-weight: 700;">${a.titulo}</p><p style="font-size: 0.8rem; color: var(--text-muted);">${a.fechaHora.split('T')[1].substring(0,5)} | ${a.sala}</p>`;
                    list.appendChild(card);
                });
            }
        }
    } catch (e) { console.error(e); }
}

async function initAttendanceChart() {
    const ctx = document.getElementById('attendanceChart');
    if (!ctx) return;

    try {
        const response = await fetch('http://localhost:8081/api/reports/stats');
        if (!response.ok) throw new Error('Error al cargar estadísticas');
        
        const data = await response.json();
        const attendanceData = data.attendance || {};
        
        const labels = Object.keys(attendanceData);
        const values = Object.values(attendanceData);

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Asistentes por Clase',
                    data: values,
                    backgroundColor: 'rgba(187, 255, 0, 0.2)',
                    borderColor: '#BBFF00',
                    borderWidth: 2,
                    borderRadius: 8,
                    barPercentage: 0.6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: '#1a1a1a',
                        titleColor: '#BBFF00',
                        bodyColor: '#fff',
                        borderColor: '#333',
                        borderWidth: 1
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { color: 'rgba(255, 255, 255, 0.05)' },
                        ticks: { color: '#94A3B8' }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: '#94A3B8' }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error init chart:', error);
    }
}

async function loadStats() {
    try {
        const response = await fetch('http://localhost:8081/api/clientes');
        if (!response.ok) return;
        const clients = await response.json();
        
        const totalElem = document.getElementById('statTotalClients');
        const activeElem = document.getElementById('statActiveClients');
        if (totalElem) totalElem.innerText = clients.length;
        if (activeElem) activeElem.innerText = clients.filter(c => c.estado === 'ACTIVO').length;
        
        // Fetch real payments
        const payResp = await fetch('http://localhost:8081/api/pagos');
        const shopResp = await fetch('http://localhost:8081/api/compras');
        
        let total = 0;
        
        if (payResp.ok) {
            const payments = await payResp.json();
            total += payments.reduce((acc, p) => acc + p.monto, 0);
        }
        
        if (shopResp.ok) {
            const purchases = await shopResp.json();
            total += purchases.reduce((acc, p) => acc + p.precio, 0);
        }
        
        const payElem = document.getElementById('statTodayPayments');
        if (payElem) payElem.innerText = `${total.toFixed(2)}€`;
        
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

async function loadRecentPurchases() {
    const tableBody = document.getElementById('purchasesTableBody');
    if (!tableBody) return;
    
    tableBody.innerHTML = '<tr><td colspan="4">Cargando ventas...</td></tr>';
    
    try {
        const response = await fetch('http://localhost:8081/api/compras');
        if (!response.ok) return;
        const purchases = await response.json();
        
        tableBody.innerHTML = '';
        
        if (purchases.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="4">No hay ventas registradas.</td></tr>';
            return;
        }
        
        // Show last 5
        purchases.slice(0, 5).forEach(purchase => {
            const row = document.createElement('tr');
            const fecha = new Date(purchase.fecha).toLocaleDateString('es-ES', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' });
            row.innerHTML = `
                <td>${fecha}</td>
                <td>${purchase.usuario ? purchase.usuario.username : 'Anónimo'}</td>
                <td>${purchase.producto}</td>
                <td>${purchase.precio.toFixed(2)}€</td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading recent purchases:', error);
        tableBody.innerHTML = '<tr><td colspan="4" style="color:red;">Error al cargar ventas</td></tr>';
    }
}

async function loadRecentClients() {
    const tableBody = document.getElementById('clientsTableBody');
    if (!tableBody) return;
    
    tableBody.innerHTML = '<tr><td colspan="4">Cargando clientes...</td></tr>';

    try {
        const response = await fetch('http://localhost:8081/api/clientes');
        const clients = await response.json();
        
        tableBody.innerHTML = '';
        
        if (clients.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="4">No hay clientes registrados.</td></tr>';
            return;
        }

        // Show last 5
        clients.slice().reverse().slice(0, 5).forEach(client => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${client.nombre} ${client.apellidos}</td>
                <td>${client.dni}</td>
                <td><span class="badge ${client.estado === 'ACTIVO' ? 'badge-success' : 'badge-warning'}">${client.estado}</span></td>
                <td>
                    <button class="btn-sm btn-outline-sm" onclick="editClient(${client.id})"><i class="fas fa-edit"></i></button>
                    <button class="btn-sm btn-outline-sm" style="color: var(--danger);" onclick="deleteClient(${client.id})"><i class="fas fa-trash"></i></button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading clients:', error);
        tableBody.innerHTML = '<tr><td colspan="4" style="color:red;">Error al conectar con la API</td></tr>';
    }
}

async function editClient(id) {
    try {
        const response = await fetch(`http://localhost:8081/api/clientes/${id}`);
        const client = await response.json();
        
        document.getElementById('modalTitle').innerText = 'Editar Cliente';
        document.getElementById('editClientId').value = client.id;
        document.getElementById('newNombre').value = client.nombre;
        document.getElementById('newApellidos').value = client.apellidos;
        document.getElementById('newDni').value = client.dni;
        document.getElementById('newTelefono').value = client.telefono || '';
        document.getElementById('newEmail').value = client.email || '';
        
        document.getElementById('clientModal').style.display = 'flex';
    } catch (error) {
        alert('Error al cargar datos del cliente');
    }
}

async function deleteClient(id) {
    if (!confirm('¿Estás seguro de eliminar este cliente? Se borrarán también sus pagos y rutinas.')) return;
    
    try {
        const response = await fetch(`http://localhost:8081/api/clientes/${id}`, { method: 'DELETE' });
        if (response.ok) {
            loadStats();
            loadRecentClients();
        } else {
            alert('Error al eliminar cliente');
        }
    } catch (error) {
        console.error('Error delete:', error);
    }
}

async function loadClientDashboardData(clienteId) {
    try {
        // 1. Routine Info
        const routineResp = await fetch(`http://localhost:8081/api/rutinas/cliente/${clienteId}`);
        const routines = await routineResp.json();
        const routineNameElem = document.getElementById('cardRoutineName');
        if (routineNameElem) {
            if (routines.length > 0) {
                routineNameElem.innerText = routines[routines.length - 1].nombreRutina;
            } else {
                routineNameElem.innerText = "Sin asignar";
            }
        }

        // 2. Payment Info
        const paymentResp = await fetch(`http://localhost:8081/api/pagos/cliente/${clienteId}`);
        const payments = await paymentResp.json();
        const statusElem = document.getElementById('cardPaymentStatus');
        const statusCard = document.getElementById('paymentStatusCard');
        
        if (statusElem && statusCard) {
            if (payments.length > 0) {
                statusElem.innerText = "Al día";
                statusCard.style.borderLeftColor = "var(--success)";
            } else {
                statusElem.innerText = "Pendiente";
                statusCard.style.borderLeftColor = "var(--danger)";
            }
        }
    } catch (error) {
        console.error('Error loading client dashboard data:', error);
    }
}

async function buyItem(productName, price) {
    const btn = event.target;
    const originalText = btn.innerHTML;
    
    // 1. Loading State
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';
    btn.style.opacity = '0.7';

    // 2. Simulate Network Delay
    await new Promise(resolve => setTimeout(resolve, 1500));

    try {
        const userData = JSON.parse(localStorage.getItem('gym_user'));
        const response = await fetch('http://localhost:8081/api/compras', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                producto: productName,
                precio: price,
                usuarioId: userData ? userData.id : 1
            })
        });

        if (response.ok) {
            // 3. Success State
            btn.innerHTML = '<i class="fas fa-check"></i> ¡Comprado!';
            btn.style.background = 'var(--accent)';
            btn.style.color = '#000';
            
            // Show a nice notification (could be a modal, using alert for now but formatted)
            setTimeout(() => {
                alert(`¡Gracias por tu compra!\nHas adquirido: ${productName}\nPrecio: ${price}€\n\nEl cargo se aplicará a tu próxima cuota mensual.`);
                
                // Reset button after some time
                setTimeout(() => {
                    btn.disabled = false;
                    btn.innerHTML = originalText;
                    btn.style.background = '';
                    btn.style.color = '';
                    btn.style.opacity = '';
                }, 2000);
            }, 500);
        } else {
            throw new Error('Error en el servidor');
        }
    } catch (error) {
        console.error('Error buying item:', error);
        btn.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Error';
        btn.style.background = 'var(--danger)';
        setTimeout(() => {
            btn.disabled = false;
            btn.innerHTML = originalText;
            btn.style.background = '';
            btn.style.opacity = '';
        }, 3000);
    }
}

