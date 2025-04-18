
async function loadUsers() {
    const response = await fetch('/api/users');
    const users = await response.json();
    const tbody = document.getElementById('user-table-body');
    tbody.innerHTML = '';

    users.forEach(user => {
        const row = document.createElement('tr');

        const roles = user.roles.map(role => role.name.replace('ROLE_', '')).join(' ');

        row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.firstName}</td>
                <td>${user.lastName}</td>
                <td>${user.age}</td>
                <td>${user.email}</td>
                <td>${roles}</td>
                <td><button class="btn btn-info btn-sm" onclick="editUser(${user.id})">Edit</button></td>
                <td><button class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">Delete</button></td>
            `;

        tbody.appendChild(row);
    });
}

async function loadRoles(selectElementId) {
    const response = await fetch('/api/roles');
    const roles = await response.json();

    const select = document.getElementById(selectElementId);
    select.innerHTML = '';

    roles.forEach(role => {
        const option = document.createElement('option');
        option.text = role.name.replace('ROLE_', '');
        option.value = role.name;
        select.appendChild(option);
    });
}

window.addEventListener('DOMContentLoaded', async () => {
    const access = await checkAccess();
    if (!access) return;

    await loadUsers();
    await loadRoles('roles');
    await loadCurrentUserInfo();
});


document.getElementById('addUserForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const form = e.target;

    const selectedRoles = Array.from(form.roles.selectedOptions)
        .map(opt => 'ROLE_' + opt.text.trim());

    const userData = {
        firstName: form.firstName.value,
        lastName: form.lastName.value,
        age: parseInt(form.age.value),
        email: form.email.value,
        password: form.password.value,
        roles: selectedRoles
    };

    const response = await fetch('/api/users', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(userData)
    });

    if (response.ok) {
        form.reset();
        await loadUsers();
        document.getElementById('users-tab').click();
    } else {
        const errorText = await response.text();
        console.error('Failed to add user:', errorText);
        alert('Failed to add user');
    }
});

async function editUser(id) {
    const response = await fetch(`/api/users/${id}`);
    const user = await response.json();

    // Заполняем поля модального окна
    document.getElementById('edit-id').value = user.id;
    document.getElementById('edit-firstName').value = user.firstName;
    document.getElementById('edit-lastName').value = user.lastName;
    document.getElementById('edit-age').value = user.age;
    document.getElementById('edit-email').value = user.email;

    // Роли
    const rolesSelect = document.getElementById('edit-roles');
    const responseRoles = await fetch('/api/roles');
    const allRoles = await responseRoles.json();

    rolesSelect.innerHTML = '';
    allRoles.forEach(role => {
        const option = document.createElement('option');
        option.text = role.name.replace('ROLE_', '');
        option.value = role.name;
        if (user.roles.map(r => r.name).includes(role.name)) {
            option.selected = true;
        }
        rolesSelect.appendChild(option);
    });

    // Показываем модалку
    const modal = new bootstrap.Modal(document.getElementById('editModal'));
    modal.show();
}

document.getElementById('editUserForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const id = document.getElementById('edit-id').value;
    const firstName = document.getElementById('edit-firstName').value;
    const lastName = document.getElementById('edit-lastName').value;
    const age = parseInt(document.getElementById('edit-age').value);
    const email = document.getElementById('edit-email').value;
    const password = document.getElementById('edit-password').value;

    const selectedRoles = Array.from(document.getElementById('edit-roles').selectedOptions)
        .map(opt => opt.value);

    const userData = {
        id,
        firstName,
        lastName,
        age,
        email,
        roles: selectedRoles
    };

    if (password.trim() !== '') {
        userData.password = password;
    }

    const response = await fetch(`/api/users/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(userData)
    });

    if (response.ok) {
        await loadUsers();
        bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
    } else {
        const errorText = await response.text();
        console.error('Failed to update user:', errorText);
        alert('Failed to update user');
    }
});

async function deleteUser(id) {
    const response = await fetch(`/api/users/${id}`);
    const user = await response.json();

    document.getElementById('delete-id').value = user.id;
    document.getElementById('delete-firstName').value = user.firstName;
    document.getElementById('delete-lastName').value = user.lastName;
    document.getElementById('delete-age').value = user.age;
    document.getElementById('delete-email').value = user.email;

    const rolesSelect = document.getElementById('delete-roles');
    rolesSelect.innerHTML = '';
    user.roles.forEach(role => {
        const option = document.createElement('option');
        option.text = role.name.replace('ROLE_', '');
        rolesSelect.appendChild(option);
    });

    const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
    modal.show();
}

document.getElementById('deleteUserForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const id = document.getElementById('delete-id').value;

    const response = await fetch(`/api/users/${id}`, {
        method: 'DELETE'
    });

    if (response.ok) {
        await loadUsers();
        bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
    } else {
        const errorText = await response.text();
        console.error('Failed to delete user:', errorText);
        alert('Failed to delete user');
    }
});

async function loadCurrentUserInfo() {
    const response = await fetch('/api/users/current', {
        method: 'GET',
        cache: 'no-store',
        credentials: 'same-origin'
    });
    if (!response.ok) {
        console.error('Failed to load current user info:', response.status);
        return;
    }
    const user = await response.json();

    const roles = user.roles.map(r => r.name.replace('ROLE_', '')).join(' ');

    document.getElementById('current-email').textContent = user.email;
    document.getElementById('current-roles').textContent = roles;

    const userInfoRow = document.querySelector('#current-user-table-body tr');
    userInfoRow.innerHTML = `
        <td>${user.id}</td>
        <td>${user.firstName}</td>
        <td>${user.lastName}</td>
        <td>${user.age}</td>
        <td>${user.email}</td>
        <td>${roles}</td>
    `;
}

async function checkAccess() {
    const response = await fetch('/api/users/current', { cache: 'no-store', credentials: 'same-origin' });
    if (!response.ok) {
        window.location.href = '/login';
        return false;
    }
    const user = await response.json();
    const roles = user.roles.map(r => r.name);

    if (!roles.includes("ROLE_ADMIN")) {
        if (response.status === 403) {
            window.location.href = "/access-denied";
        }
        return false;
    }

    return true;
}


