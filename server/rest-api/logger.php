<?php
class Logger {

    public static function log($message) {
        try {
            $conn = mysqli_connect('192.168.2.74', 'ada', 'ada', 'ada5');
            $query = sprintf("insert into logging (log) values ('%s')", mysqli_real_escape_string($conn, $message));
            $conn->query($query);
        }
        finally {
            $conn->close();
        }

    }
}
?>