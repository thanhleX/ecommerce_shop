import useAuthStore from '../store/authStore';

const usePermission = () => {
    const { permissions } = useAuthStore();

    /**
     * Check if user has a specific permission
     * @param {string} permission - The permission to check (e.g., 'product:create', 'ROLE_ADMIN')
     * @returns {boolean} - True if user has permission, false otherwise
     */
    const hasPermission = (permission) => {
        if (!permissions || !Array.isArray(permissions)) return false;
        return permissions.includes(permission);
    };

    /**
     * Check if user has ALL of the specified permissions
     * @param {string[]} requiredPermissions - Array of permissions to check
     * @returns {boolean}
     */
    const hasAllPermissions = (requiredPermissions) => {
        if (!permissions || !Array.isArray(permissions)) return false;
        if (!requiredPermissions || requiredPermissions.length === 0) return true;
        return requiredPermissions.every(p => permissions.includes(p));
    };

    /**
     * Check if user has AT LEAST ONE of the specified permissions
     * @param {string[]} requiredPermissions - Array of permissions to check
     * @returns {boolean}
     */
    const hasAnyPermission = (requiredPermissions) => {
        if (!permissions || !Array.isArray(permissions)) return false;
        if (!requiredPermissions || requiredPermissions.length === 0) return true;
        return requiredPermissions.some(p => permissions.includes(p));
    };

    return {
        permissions,
        hasPermission,
        hasAllPermissions,
        hasAnyPermission,
    };
};

export default usePermission;
