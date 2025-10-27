const API_BASE = "http://localhost:8080";

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }) // coincide con LoginRequest
        });

        if (res.ok) {
            const data = await res.json();
            localStorage.setItem('jwt', data.idToken);
            alert('Login OK — token saved to localStorage.');
        } else {
            const err = await res.json();
            alert('Login failed: ' + (err.error || JSON.stringify(err)));
        }
    } catch (error) {
        alert('Error de conexión: ' + error.message);
    }
});

// Función opcional para probar endpoints protegidos
async function callProtected() {
    const token = localStorage.getItem('jwt');
    const res = await fetch(`${API_BASE}/protected`, {
        headers: { 'Authorization': 'Bearer ' + token }
    });
    console.log(await res.text());
}
