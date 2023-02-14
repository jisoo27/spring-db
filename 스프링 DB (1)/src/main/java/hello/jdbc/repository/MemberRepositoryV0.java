package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

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

    private void close(Connection con, Statement stmt, ResultSet rs) {

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
}
