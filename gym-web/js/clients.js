document.addEventListener('DOMContentLoaded', () => {
    const userData = JSON.parse(localStorage.getItem('gym_user'));
    if (!userData) {
        window.location.href = 'index.html';
        return;
    }

    if (userData.role !== 'ADMIN') {
        window.location.href = 'dashboard.html';
        return;
    }

    document.getElementById('adminName').innerText = userData.username;
    document.getElementById('adminAvatar').innerText = userData.username.charAt(0).toUpperCase();

    loadClients();

    // Logout
    document.getElementById('btnLogout').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('gym_user');
        window.location.href = 'index.html';
    });

    // Modal Logic
    const modal = document.getElementById('clientModal');
    const btnAdd = document.getElementById('btnAddClient');
    const btnClose = document.getElementById('closeClientModal');
    const form = document.getElementById('clientForm');

    btnAdd.onclick = () => {
        document.getElementById('modalTitle').innerText = 'Nuevo Cliente';
        form.reset();
        document.getElementById('clientId').value = '';
        modal.style.display = 'flex';
    };

    btnClose.onclick = () => modal.style.display = 'none';

    form.onsubmit = async (e) => {
        e.preventDefault();
        const id = document.getElementById('clientId').value;
        const data = {
            nombre: document.getElementById('nombre').value,
            apellidos: document.getElementById('apellidos').value,
            dni: document.getElementById('dni').value,
            email: document.getElementById('email').value,
            telefono: document.getElementById('telefono').value,
            estado: document.getElementById('estado').value,
            observaciones: document.getElementById('observaciones').value
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
                modal.style.display = 'none';
                loadClients();
            } else {
                alert('Error al guardar el cliente');
            }
        } catch (err) {
            console.error(err);
            alert('Error de conexión con el servidor');
        }
    };
});

async function loadClients() {
    const tableBody = document.getElementById('clientsTableBody');
    tableBody.innerHTML = '<tr><td colspan="5">Cargando...</td></tr>';

    try {
        const resp = await fetch('http://localhost:8081/api/clientes');
        if (resp.ok) {
            const clients = await resp.json();
            tableBody.innerHTML = '';
            clients.forEach(c => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${c.nombre} ${c.apellidos}</td>
                    <td>${c.dni}</td>
                    <td>${c.email || '-'}</td>
                    <td><span class="badge ${c.estado === 'ACTIVO' ? 'badge-success' : 'badge-warning'}">${c.estado}</span></td>
                    <td>
                        <button class="btn-sm btn-outline-sm" onclick="editClient(${c.id})"><i class="fas fa-edit"></i></button>
                        <button class="btn-sm btn-outline-sm" style="color: var(--danger);" onclick="deleteClient(${c.id})"><i class="fas fa-trash"></i></button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        }
    } catch (err) {
        console.error(err);
        tableBody.innerHTML = '<tr><td colspan="5" style="color:red;">Error al cargar datos</td></tr>';
    }
}

async function editClient(id) {
    try {
        const resp = await fetch(`http://localhost:8081/api/clientes/${id}`);
        if (resp.ok) {
            const c = await resp.json();
            document.getElementById('modalTitle').innerText = 'Editar Cliente';
            document.getElementById('clientId').value = c.id;
            document.getElementById('nombre').value = c.nombre;
            document.getElementById('apellidos').value = c.apellidos;
            document.getElementById('dni').value = c.dni;
            document.getElementById('email').value = c.email || '';
            document.getElementById('telefono').value = c.telefono || '';
            document.getElementById('estado').value = c.estado;
            document.getElementById('observaciones').value = c.observaciones || '';
            
            document.getElementById('clientModal').style.display = 'flex';
        }
    } catch (err) { console.error(err); }
}

async function deleteClient(id) {
    if (!confirm('¿Estás seguro de eliminar este cliente?')) return;
    try {
        const resp = await fetch(`http://localhost:8081/api/clientes/${id}`, { method: 'DELETE' });
        if (resp.ok) loadClients();
    } catch (err) { console.error(err); }
}
