package com.example.smoking.dao; // Điều chỉnh package name cho đúng

import com.example.smoking.model.User; // Import class User đã hoàn chỉnh
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException; // Để xử lý trường hợp không tìm thấy kết quả
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional; // Sử dụng Optional để xử lý trường hợp không tìm thấy đối tượng

public class UserDAO {

    // Sử dụng static cho EntityManagerFactory để chỉ khởi tạo một lần duy nhất cho toàn bộ ứng dụng
    private static EntityManagerFactory entityManagerFactory;
    private final String persistenceUnitName; // Để lưu tên persistence unit

    public UserDAO(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        // Chỉ khởi tạo nếu chưa có hoặc đã đóng
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory(this.persistenceUnitName);
        }
    }

    // Phương thức trợ giúp để lấy EntityManager
    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Lưu một đối tượng User mới vào cơ sở dữ liệu.
     * @param user Đối tượng User cần lưu.
     * @return Optional chứa đối tượng User đã được lưu (có ID), rỗng nếu thất bại.
     */
    public Optional<User> save(User user) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(user); // Gán ID cho user sau khi persist
            entityManager.getTransaction().commit();
            return Optional.of(user);
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Lỗi khi lưu User: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * Tìm một đối tượng User theo ID.
     * @param id ID của User.
     * @return Optional chứa đối tượng User nếu tìm thấy, rỗng nếu không tìm thấy.
     */
    public Optional<User> findById(Long id) {
        EntityManager entityManager = getEntityManager();
        try {
            User user = entityManager.find(User.class, id);
            return Optional.ofNullable(user); // Trả về Optional.empty nếu user là null
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm User theo ID: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * Tìm một đối tượng User theo username.
     * @param username Tên đăng nhập của User.
     * @return Optional chứa đối tượng User nếu tìm thấy, rỗng nếu không tìm thấy.
     */
    public Optional<User> findByUsername(String username) {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            // Không tìm thấy user nào với username này
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm User theo username: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * Tìm một đối tượng User theo email.
     * @param email Email của User.
     * @return Optional chứa đối tượng User nếu tìm thấy, rỗng nếu không tìm thấy.
     */
    public Optional<User> findByEmail(String email) {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm User theo email: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * Lấy tất cả các đối tượng User trong cơ sở dữ liệu.
     * @return Danh sách các đối tượng User, rỗng nếu không có User nào.
     */
    public List<User> findAll() {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy tất cả User: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Trả về danh sách rỗng thay vì null
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * Cập nhật một đối tượng User đã tồn tại trong cơ sở dữ liệu.
     * @param user Đối tượng User cần cập nhật (phải có ID).
     * @return Optional chứa đối tượng User đã được cập nhật, rỗng nếu thất bại.
     */
    public Optional<User> update(User user) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            // merge() trả về đối tượng user đã được gắn kết với context hiện tại
            User updatedUser = entityManager.merge(user);
            entityManager.getTransaction().commit();
            return Optional.of(updatedUser);
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Lỗi khi cập nhật User: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * Xóa một đối tượng User khỏi cơ sở dữ liệu theo ID.
     * @param id ID của User cần xóa.
     * @return true nếu xóa thành công, false nếu không tìm thấy hoặc lỗi.
     */
    public boolean delete(Long id) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            User user = entityManager.find(User.class, id);
            if (user != null) {
                entityManager.remove(user);
                entityManager.getTransaction().commit();
                return true;
            } else {
                System.out.println("Không tìm thấy User với ID: " + id + " để xóa.");
                return false;
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("Lỗi khi xóa User: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * Đóng EntityManagerFactory. Nên gọi khi ứng dụng tắt.
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("EntityManagerFactory đã đóng.");
        }
    }
}