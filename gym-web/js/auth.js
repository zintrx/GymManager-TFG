document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const loginBtn = document.getElementById('loginBtn');
    const errorMessage = document.getElementById('errorMessage');
    
    // UI Loading state
    loginBtn.innerText = 'Cargando...';
    loginBtn.style.opacity = '0.7';
    loginBtn.disabled = true;
    errorMessage.style.display = 'none';

    try {
        const response = await fetch('http://localhost:8081/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            // Store user session (simple version)
            localStorage.setItem('gym_user', JSON.stringify(data));
            
            // Redirect to dashboard
            window.location.href = 'dashboard.html';
        } else {
            errorMessage.style.display = 'block';
            errorMessage.innerText = data.message || 'Error de conexión';
        }
    } catch (error) {
        console.error('Error logging in:', error);
        errorMessage.style.display = 'block';
        errorMessage.innerText = 'No se pudo conectar con el servidor.';
    } finally {
        loginBtn.innerText = 'Entrar';
        loginBtn.style.opacity = '1';
        loginBtn.disabled = false;
    }
});
