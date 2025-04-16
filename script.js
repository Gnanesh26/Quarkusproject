// Wait for the DOM to load
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const usernameField = document.getElementById('username');
    const passwordField = document.getElementById('password');

    // Handle form submission
    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Prevent page reload

        // Get values from input fields
        const username = usernameField.value.trim();
        const password = passwordField.value.trim();
        console.log(username, password)
        // Simple client-side validation
        if (!username || !password) {
            alert('Please fill in both fields!');
            return;
        }

        try {
            // Send the data to a backend API (modify URL as needed)
            const response = await fetch('http://localhost:8080/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password }),
            });

            // Handle the response
            if (response.ok) {
                alert('Login successful!');
            } else {
                alert('Invalid username or password.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
        }
    });
});
