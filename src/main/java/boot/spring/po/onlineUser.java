package boot.spring.po;
import org.ietf.jgss.Oid;

public class onlineUser {

    Oid _id;
    private String onliner;

    public class Oid
    {
        String $oid;
        public String get$oid() {
            return $oid;
        }

        public void set$oid(String $oid) {
            this.$oid = $oid;
        }
    }

    public void setonliner(String username) {
        this.onliner = username;
    }

    public String getonliner() {
        return onliner;
    }
}