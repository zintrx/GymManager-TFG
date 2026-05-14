document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('registerForm');
    const errorMessage = document.getElementById('errorMessage');
    
    // Configuración base
    const API_BASE_URL = 'http://localhost:8081/api';
    
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const usernameInput = document.getElementById('username');
            const emailInput = document.getElementById('email');
            const passwordInput = document.getElementById('password');
            const btnSubmit = document.getElementById('registerBtn');
            
            const username = usernameInput.value.trim();
            const email = emailInput.value.trim();
            const password = passwordInput.value;
            
            // Basic validation
            if (!username || !email || !password) {
                showError('Por favor, completa todos los campos.');
                return;
            }
            
            // Loading state
            const originalText = btnSubmit.innerHTML;
            btnSubmit.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Registrando...';
            btnSubmit.disabled = true;
            errorMessage.style.display = 'none';
            
            try {
                const response = await fetch(`${API_BASE_URL}/auth/register`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ username, email, password })
                });
                
                const data = await response.json();
                
                if (response.ok && data.success) {
                    // Registration successful
                    alert('¡Cuenta creada con éxito! Ahora puedes iniciar sesión.');
                    window.location.href = 'index.html';
                } else {
                    // Server returned error
                    showError(data.message || 'Error al registrar usuario. Inténtalo de nuevo.');
                    btnSubmit.innerHTML = originalText;
                    btnSubmit.disabled = false;
                }
                
            } catch (error) {
                console.error('Error de conexión:', error);
                showError('Error de conexión con el servidor. Verifica que el backend esté en ejecución.');
                btnSubmit.innerHTML = originalText;
                btnSubmit.disabled = false;
            }
        });
    }
    
    function showError(msg) {
        if (errorMessage) {
            errorMessage.textContent = msg;
            errorMessage.style.display = 'block';
            
            // Shake effect
            errorMessage.classList.remove('shake');
            void errorMessage.offsetWidth; // Trigger reflow
            errorMessage.classList.add('shake');
        }
    }
});
