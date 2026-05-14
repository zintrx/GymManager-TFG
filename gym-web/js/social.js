const API_BASE_URL = 'http://localhost:8081/api';

document.addEventListener('DOMContentLoaded', () => {
    loadPosts();
});

function switchSocialTab(tabId) {
    // Hide all tabs
    document.querySelectorAll('.social-tab-content').forEach(tab => {
        tab.style.display = 'none';
        tab.classList.remove('active');
    });
    
    // Remove active class from all buttons
    document.querySelectorAll('.tabs-scroll .tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    const selectedTab = document.getElementById(tabId + '-tab');
    if(selectedTab) {
        selectedTab.style.display = 'block';
        selectedTab.classList.add('active');
    }
    
    // Set clicked button to active
    if (event && event.currentTarget) {
        event.currentTarget.classList.add('active');
    }

    // Handle FAB visibility
    const fab = document.getElementById('createPostFab');
    if (fab) {
        if (tabId === 'feed') {
            fab.style.display = 'flex';
            loadPosts(); // Refresh posts when switching to feed
        } else {
            fab.style.display = 'none';
        }
    }
}

async function loadPosts() {
    const container = document.getElementById('posts-container');
    if (!container) return;

    try {
        const response = await fetch(`${API_BASE_URL}/publicaciones`);
        if (!response.ok) throw new Error('Error al cargar publicaciones');
        
        const posts = await response.json();
        renderPosts(posts);
    } catch (error) {
        console.error('Error:', error);
        container.innerHTML = `<p style="color: var(--danger); text-align: center; margin-top: 2rem;">No se pudieron cargar las publicaciones. Verifica que el servidor esté activo.</p>`;
    }
}

function renderPosts(posts) {
    const container = document.getElementById('posts-container');
    if (posts.length === 0) {
        container.innerHTML = `<p style="color: var(--text-muted); text-align: center; margin-top: 2rem;">No hay publicaciones todavía. ¡Sé el primero en compartir algo!</p>`;
        return;
    }

    container.innerHTML = posts.map(post => `
        <div class="post-card">
            <div class="post-header">
                <img src="${post.autor.avatarUrl || 'https://i.pravatar.cc/150?u=' + post.autor.id}" alt="Avatar" class="post-avatar">
                <div class="post-meta">
                    <span class="post-author">${post.autor.username}</span>
                    <span class="post-time">${formatDate(post.fecha)}</span>
                </div>
            </div>
            <div class="post-content">
                ${post.contenido}
            </div>
            <div class="post-actions">
                <button class="action-btn" onclick="toggleLike(this)">
                    <i class="far fa-heart"></i> <span>${post.likes || 0}</span>
                </button>
                <button class="action-btn">
                    <i class="far fa-comment"></i> <span>0</span>
                </button>
            </div>
        </div>
    `).join('');
}

function toggleLike(btn) {
    const icon = btn.querySelector('i');
    const span = btn.querySelector('span');
    let count = parseInt(span.innerText);

    if (icon.classList.contains('far')) {
        // Like
        icon.classList.remove('far');
        icon.classList.add('fas');
        icon.style.color = 'var(--primary)';
        span.innerText = count + 1;
        span.style.color = 'var(--primary)';
    } else {
        // Unlike
        icon.classList.remove('fas');
        icon.classList.add('far');
        icon.style.color = '';
        span.innerText = count - 1;
        span.style.color = '';
    }
}

// Modal Functions
function openPostModal() {
    document.getElementById('postModal').style.display = 'flex';
}

function closePostModal() {
    document.getElementById('postModal').style.display = 'none';
    document.getElementById('postContent').value = '';
}

async function submitPost() {
    const content = document.getElementById('postContent').value.trim();
    if (!content) return;

    try {
        const response = await fetch(`${API_BASE_URL}/publicaciones`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                contenido: content,
                usuarioId: 1 // Placeholder: En una app real usaríamos el ID del usuario logueado
            })
        });

        if (response.ok) {
            closePostModal();
            loadPosts(); // Refresh feed
        } else {
            alert('Error al publicar. Inténtalo de nuevo.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión con el servidor.');
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = Math.floor((now - date) / 1000); // Diff in seconds

    if (diff < 60) return 'Ahora mismo';
    if (diff < 3600) return `Hace ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `Hace ${Math.floor(diff / 3600)} h`;
    
    return date.toLocaleDateString('es-ES', { day: 'numeric', month: 'short' });
}
