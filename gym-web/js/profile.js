console.log("🚀 DEBUG: Iniciando script de perfil de GymManager...");

document.addEventListener('DOMContentLoaded', () => {
    const userData = JSON.parse(localStorage.getItem('gym_user'));
    
    if (!userData) {
        window.location.href = 'index.html';
        return;
    }

    // Poblar datos básicos
    const usernameElem = document.getElementById('profileUsername');
    const emailElem = document.getElementById('profileEmail');
    const feeElem = document.getElementById('profileFee');
    const statusElem = document.getElementById('profileStatus');
    const roleElem = document.getElementById('profileRole');
    const avatarImg = document.getElementById('profileAvatarImg');
    const pubAvatar = document.getElementById('pubAvatar1');
    const pubName = document.getElementById('pubName1');

    if (usernameElem) usernameElem.innerText = userData.username;
    if (emailElem) emailElem.innerText = userData.email || 'Sin email';
    if (feeElem) feeElem.innerText = userData.cuotaMensual ? `${userData.cuotaMensual}€` : '29.99€';
    if (statusElem) statusElem.innerText = userData.estado || 'ACTIVO';
    if (roleElem) roleElem.innerText = userData.role || 'CLIENTE';
    if (pubName) pubName.innerText = userData.username;

    // Poblar campos de ajustes
    const setEmail = document.getElementById('set_email');
    const setPhone = document.getElementById('set_phone');
    const setDni = document.getElementById('set_dni');

    if(setEmail) setEmail.value = userData.email || '';
    if(setPhone) setPhone.value = userData.telefono || '';
    if(setDni) setDni.value = userData.dni || '';

    // Cargar Avatar
    if (avatarImg) {
        if(userData.avatarUrl) {
            avatarImg.src = userData.avatarUrl;
            if(pubAvatar) pubAvatar.src = userData.avatarUrl;
        } else {
            const avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(userData.username)}&background=333&color=fff&size=128`;
            avatarImg.src = avatarUrl;
            if(pubAvatar) pubAvatar.src = avatarUrl;
        }
    }
});

async function saveSettings() {
    const userData = JSON.parse(localStorage.getItem('gym_user'));
    const email = document.getElementById('set_email').value;
    const telefono = document.getElementById('set_phone').value;
    const dni = document.getElementById('set_dni').value;
    const password = document.getElementById('set_password').value;

    const btn = document.querySelector('.btn-primary-yellow');
    const originalText = btn.innerText;
    btn.innerText = "Guardando...";
    btn.disabled = true;

    try {
        const response = await fetch('http://localhost:8081/api/auth/update', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: userData.username,
                email,
                telefono,
                dni,
                password
            })
        });

        const data = await response.json();
        if (response.ok && data.success) {
            localStorage.setItem('gym_user', JSON.stringify(data));
            alert("✅ Ajustes guardados correctamente.");
            location.reload(); 
        } else {
            alert("❌ Error: " + data.message);
        }
    } catch (e) {
        alert("Error de conexión.");
    } finally {
        btn.innerText = originalText;
        btn.disabled = false;
    }
}

async function upgradeToPlan(planName) {
    const userData = JSON.parse(localStorage.getItem('gym_user'));
    
    try {
        const response = await fetch('http://localhost:8081/api/auth/update', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: userData.username,
                role: planName.toUpperCase()
            })
        });

        const data = await response.json();
        if (data.success) {
            localStorage.setItem('gym_user', JSON.stringify(data));
            alert(`⭐ ¡Felicidades! Ahora eres miembro ${planName.toUpperCase()}.`);
            location.reload();
        } else {
            alert("Error al actualizar el plan.");
        }
    } catch (e) {
        alert("Error de conexión al mejorar plan.");
    }
}

async function updateAvatar(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = async function(e) {
            const newSrc = e.target.result;
            const userData = JSON.parse(localStorage.getItem('gym_user'));
            
            try {
                const response = await fetch('http://localhost:8081/api/auth/update', {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        username: userData.username,
                        avatarUrl: newSrc
                    })
                });
                const data = await response.json();
                if(data.success) {
                    localStorage.setItem('gym_user', JSON.stringify(data));
                    alert("📸 Foto de perfil sincronizada con éxito.");
                    location.reload();
                }
            } catch(err) {
                alert("Error al sincronizar la foto.");
            }
        }
        reader.readAsDataURL(file);
    }
}

function openSettingsModal() { document.getElementById('settingsModal').style.display = 'flex'; }
function closeSettingsModal() { document.getElementById('settingsModal').style.display = 'none'; }
function openPlanModal() { document.getElementById('planModal').style.display = 'flex'; }
function closePlanModal() { document.getElementById('planModal').style.display = 'none'; }
function openAvatarSelector() { document.getElementById('avatarInput').click(); }

function switchProfileTab(tabId, btnElement) {
    document.querySelectorAll('.profile-tab-content').forEach(tab => tab.style.display = 'none');
    document.querySelectorAll('.tabs-scroll .tab-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById('tab-' + tabId).style.display = 'block';
    btnElement.classList.add('active');
}
