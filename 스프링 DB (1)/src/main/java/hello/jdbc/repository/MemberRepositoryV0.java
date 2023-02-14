package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/*
*  JDBC - DriverManager 사용
* */

@Slf4j
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException{
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;


        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); // 위에 준비된 것들이 이메서드를 호출하면서 실행이 된다.
            return member;
            //바인딩 해주기
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            //pstmt.close(); // 여기서 Exception 이 터지면 아래 코드를 실행하지 않고 밖으로 나갈 것이다. 따라서 각각 try / catch 를 해주어야 한다.
            //con.close();
            close(con, pstmt, null); // 리소스 정리
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) { // 해제는 이 순서대로 아래의 로직대로 해제되게 된다.

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close(); // 여기에서 SQL Exception 이 터지더라도 여기서 잡아주기 때문에 아래의 코드에 영향을 주지 않는다.
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

    }


    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

    public Member findById (String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null; // finally 에서 사용하기 위해 여기에 선언해줘야한다.
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if (rs.next()) { // next를 한번은 꼭 호출해주어야 데이터가 있는지 없는지를 확인한다. 첫번째 데이터가 있으면 true 반환
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId); // 이렇게 키 값을 넣어줘야 어떤 member에서 에러가 난건지 단번에 알 수 있다.
            }

        } catch (SQLException e) {
            log.info("error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {} " , resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete (String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }
}
