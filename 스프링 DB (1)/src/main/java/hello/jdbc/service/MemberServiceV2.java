package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/*
*  트랜잭션 - 파라미터 연동, 풀을 고려한 종료
*
* */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    // 트랜잭션을 관리하는 로직
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); // 트랜잭션 시작
            // 비즈니스 로직
            bizLogic(fromId, toId, money, con);
            con.commit(); // 성공시 커밋
        } catch (Exception e) {
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }


    }

    // 비즈니스 로직
    private void bizLogic(String fromId, String toId, int money, Connection con) throws SQLException {
        Member fromMember = memberRepository.findById(fromId, con);
        Member toMember = memberRepository.findById(toId, con);

        memberRepository.update(fromId, fromMember.getMoney() - money, con);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money, con);
    }



    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); // 커넥션 풀 고려 (우리가 위해서 수동 커밋을 설정했기 때문에 커넥션 풀에 그대로 반환되면 원치않게 에러가 발생할 경우가 존재. 때문에 true 로 반환 뒤 돌려준다.)
                con.close();
            } catch (Exception e) {
                log.info("error ", e);
            }
        }
    }

    private void validation(Member member) {
        if (member.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
