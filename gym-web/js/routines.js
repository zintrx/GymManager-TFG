let currentRoutineId = null;

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
        
        // Load my routine
        if (userData.clienteId) {
            loadClientRoutine(userData.clienteId);
        }
    } else {
        document.querySelectorAll('.client-only').forEach(el => el.style.display = 'none');
        document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'block');

        // 2. Initial Data Loading (Admin only)
        loadClientsForRoutine();
        loadTemplates();
    }

    // 3. Handle Routine Type Toggle (Admin only)
    const routineType = document.getElementById('routineType');
    const clientSelectGroup = document.getElementById('clientSelectGroup');
    
    if (routineType && clientSelectGroup) {
        routineType.addEventListener('change', () => {
            if (routineType.value === 'CLIENT') {
                clientSelectGroup.style.display = 'block';
                document.getElementById('selectClienteRoutine').required = true;
            } else {
                clientSelectGroup.style.display = 'none';
                document.getElementById('selectClienteRoutine').required = false;
            }
        });
    }

    // 4. Handle Routine Header Submission (Admin only)
    const routineForm = document.getElementById('routineForm');
    if (routineForm) {
        routineForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const isClientType = routineType.value === 'CLIENT';
            const clientId = document.getElementById('selectClienteRoutine').value;

            const routineData = {
                cliente: isClientType ? { id: clientId } : null,
                nombreRutina: document.getElementById('routineName').value,
                descripcion: document.getElementById('routineDesc').value
            };

            try {
                const response = await fetch('http://localhost:8081/api/rutinas', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(routineData)
                });

                if (response.ok) {
                    const data = await response.json();
                    currentRoutineId = data.id;
                    alert('Cabecera de rutina creada. Ahora añade ejercicios.');
                    
                    const builderArea = document.getElementById('exerciseBuilderArea');
                    builderArea.style.opacity = '1';
                    builderArea.style.pointerEvents = 'all';

                    if (!isClientType) loadTemplates();
                } else {
                    alert('Error al crear la rutina');
                }
            } catch (error) {
                console.error('Error:', error);
            }
        });
    }

    // 5. Handle Exercise Submission (Admin only)
    const exerciseForm = document.getElementById('exerciseForm');
    if (exerciseForm) {
        exerciseForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            if (!currentRoutineId) {
                alert('Crea primero la cabecera de la rutina');
                return;
            }

            const exData = {
                rutina: { id: currentRoutineId },
                nombre: document.getElementById('exName').value,
                series: document.getElementById('exSeries').value,
                repeticiones: document.getElementById('exReps').value,
                peso: document.getElementById('exWeight').value
            };

            try {
                const response = await fetch('http://localhost:8081/api/ejercicios', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(exData)
                });

                if (response.ok) {
                    document.getElementById('exerciseForm').reset();
                    loadExercises(currentRoutineId);
                } else {
                    alert('Error al añadir ejercicio');
                }
            } catch (error) {
                console.error('Error:', error);
            }
        });
    }

    // 6. Assignment Modal Logic (Admin only)
    const closeModal = document.getElementById('closeModal');
    if (closeModal) {
        closeModal.onclick = () => {
            document.getElementById('assignModal').style.display = 'none';
        };
    }

    const assignForm = document.getElementById('assignForm');
    if (assignForm) {
        assignForm.onsubmit = async (e) => {
            e.preventDefault();
            const templateId = document.getElementById('assignTemplateId').value;
            const clientId = document.getElementById('selectClienteAssign').value;

            try {
                const response = await fetch(`http://localhost:8081/api/rutinas/${templateId}/asignar/${clientId}`, {
                    method: 'POST'
                });

                if (response.ok) {
                    alert('Rutina asignada correctamente al cliente.');
                    document.getElementById('assignModal').style.display = 'none';
                } else {
                    alert('Error al asignar la rutina.');
                }
            } catch (error) {
                console.error('Error:', error);
            }
        };
    }

    // Logout
    document.getElementById('btnLogout').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('gym_user');
        window.location.href = 'index.html';
    });
});

async function loadTemplates() {
    const templatesList = document.getElementById('templatesList');
    try {
        const response = await fetch('http://localhost:8081/api/rutinas/plantillas');
        const templates = await response.json();
        
        if (templates.length === 0) {
            templatesList.innerHTML = `
                <div class="empty-state" style="grid-column: 1/-1; text-align: center; padding: 2rem; background: var(--bg-card); border-radius: 12px; border: 1px dashed var(--border);">
                    <p style="color: var(--text-muted);">No hay plantillas creadas todavía.</p>
                </div>
            `;
            return;
        }

        templatesList.innerHTML = '';
        templates.forEach(tpl => {
            const card = document.createElement('div');
            card.className = 'data-table-container template-card';
            card.style.padding = '1.5rem';
            card.style.borderLeft = '4px solid var(--primary)';
            card.innerHTML = `
                <div style="display: flex; justify-content: space-between; align-items: start;">
                    <h4 style="margin:0; font-size: 1.1rem; color: white;">${tpl.nombreRutina}</h4>
                    <button class="btn-sm" style="color: var(--primary)" onclick="openAssignModal(${tpl.id}, '${tpl.nombreRutina}')">
                        <i class="fas fa-user-plus"></i> ASIGNAR
                    </button>
                </div>
                <p style="font-size: 0.9rem; color: var(--text-muted); margin: 0.8rem 0;">${tpl.descripcion || 'Sin descripción'}</p>
                <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 1rem; padding-top: 1rem; border-top: 1px solid var(--border);">
                    <span style="font-size: 0.8rem; color: var(--primary);"><i class="fas fa-dumbbell"></i> Modelo Base</span>
                    <button class="btn-sm" style="color: var(--danger)" onclick="deleteTemplate(${tpl.id})"><i class="fas fa-trash"></i></button>
                </div>
            `;
            templatesList.appendChild(card);
        });
    } catch (error) {
        console.error('Error loading templates:', error);
    }
}

async function loadClientsForRoutine() {
    const select1 = document.getElementById('selectClienteRoutine');
    const select2 = document.getElementById('selectClienteAssign');
    try {
        const response = await fetch('http://localhost:8081/api/clientes');
        const clients = await response.json();
        
        const options = '<option value="">Selecciona un cliente...</option>' + 
            clients.map(c => `<option value="${c.id}">${c.nombre} ${c.apellidos}</option>`).join('');
        
        select1.innerHTML = options;
        select2.innerHTML = options;
    } catch (error) {
        console.error('Error loading clients:', error);
    }
}

function openAssignModal(id, name) {
    document.getElementById('assignTemplateId').value = id;
    document.getElementById('assignTemplateName').textContent = name;
    document.getElementById('assignModal').style.display = 'flex';
}

async function deleteTemplate(id) {
    if (!confirm('¿Seguro que quieres eliminar esta plantilla?')) return;
    try {
        const response = await fetch(`http://localhost:8081/api/rutinas/${id}`, { method: 'DELETE' });
        if (response.ok) {
            loadTemplates();
        }
    } catch (error) {
        console.error('Error deleting template:', error);
    }
}

async function loadExercises(routineId) {
    const tableBody = document.getElementById('exercisesTableBody');
    try {
        const response = await fetch(`http://localhost:8081/api/ejercicios/rutina/${routineId}`);
        const exercises = await response.json();
        
        tableBody.innerHTML = '';
        exercises.forEach(ex => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${ex.nombre}</td>
                <td>${ex.series}</td>
                <td>${ex.repeticiones}</td>
                <td>${ex.peso}kg</td>
                <td><button class="btn-sm" style="color:var(--danger)" onclick="deleteExercise(${ex.id})"><i class="fas fa-trash"></i></button></td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading exercises:', error);
    }
}

async function deleteExercise(id) {
    if (!confirm('¿Borrar ejercicio?')) return;
    try {
        const response = await fetch(`http://localhost:8081/api/ejercicios/${id}`, { method: 'DELETE' });
        if (response.ok) {
            loadExercises(currentRoutineId);
        }
    } catch (error) {
        console.error('Error deleting exercise:', error);
    }
}

async function loadClientRoutine(clienteId) {
    const container = document.getElementById('clientRoutinesContainer');
    
    try {
        const response = await fetch(`http://localhost:8081/api/rutinas/cliente/${clienteId}`);
        const routines = await response.json();
        
        if (routines.length === 0) {
            container.innerHTML = '<p style="color: var(--text-muted); text-align: center; grid-column: 1/-1; padding: 2rem;">No tienes rutinas asignadas actualmente.</p>';
            return;
        }

        container.innerHTML = '';
        // Mostramos las rutinas ordenadas por fecha (la más reciente primero)
        routines.sort((a, b) => new Date(b.fechaAsignacion) - new Date(a.fechaAsignacion));

        for (const routine of routines) {
            const card = document.createElement('div');
            card.className = 'routine-card-client';
            
            // Format date DD/MM/YYYY
            const dateStr = formatDate(routine.fechaAsignacion);

            card.innerHTML = `
                <div class="routine-card-header">
                    <div class="routine-info-main">
                        <h3 class="routine-title">${routine.nombreRutina}</h3>
                        <p class="routine-desc">${routine.descripcion || 'Sin descripción'}</p>
                        <span class="routine-date">Asignada: ${dateStr}</span>
                    </div>
                    <div class="routine-actions">
                        <button class="delete-routine-btn" onclick="deleteClientRoutine(${routine.id}, event)" title="Eliminar rutina">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </div>
                </div>
                <div class="routine-exercises-list" id="exercises-for-${routine.id}">
                    <p style="font-size: 0.8rem; color: var(--text-muted);">Cargando ejercicios...</p>
                </div>
            `;
            container.appendChild(card);
            
            // Load exercises for this card
            loadExercisesForClientCard(routine.id);
        }
    } catch (error) {
        console.error('Error loading client routines:', error);
        container.innerHTML = '<p style="color: var(--danger); text-align: center; grid-column: 1/-1;">Error al cargar las rutinas.</p>';
    }
}

async function loadExercisesForClientCard(routineId) {
    const exList = document.getElementById(`exercises-for-${routineId}`);
    try {
        const response = await fetch(`http://localhost:8081/api/ejercicios/rutina/${routineId}`);
        const exercises = await response.json();
        
        if (exercises.length === 0) {
            exList.innerHTML = '<p style="font-size: 0.8rem; color: var(--text-muted);">No hay ejercicios en esta rutina.</p>';
            return;
        }

        exList.innerHTML = exercises.map(ex => `
            <div class="exercise-item-client">
                <span class="ex-name">${ex.nombre}</span>
                <span class="ex-stats">${ex.series}x${ex.repeticiones}${ex.peso > 0 ? `, ${ex.peso}kg` : ''}</span>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading exercises for card:', error);
        exList.innerHTML = '<p style="font-size: 0.8rem; color: var(--danger);">Error al cargar ejercicios.</p>';
    }
}

async function deleteClientRoutine(routineId, event) {
    event.stopPropagation(); // Evitar que el clic se propague (si quisiéramos abrir la tarjeta)
    
    if (!confirm('¿Estás seguro de que quieres eliminar esta rutina? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        const response = await fetch(`http://localhost:8081/api/rutinas/${routineId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            // Recargar las rutinas del cliente
            const userData = JSON.parse(localStorage.getItem('gym_user'));
            if (userData && userData.clienteId) {
                loadClientRoutine(userData.clienteId);
            }
        } else {
            alert('Error al eliminar la rutina.');
        }
    } catch (error) {
        console.error('Error deleting routine:', error);
        alert('Ocurrió un error al intentar eliminar la rutina.');
    }
}

function formatDate(dateString) {
    if (!dateString) return 'Desconocida';
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}
