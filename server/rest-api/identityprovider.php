<?php
    include('db.php');
    include('mysql.php');
    include('model.php');
    include('auth.php');

    $auth = new Auth();
    if (!$user = $auth->isAuthorized()) {
        header("HTTP/1.1 401 Unauthorized");
        exit;
    }

    $db = new MySql('192.168.2.74', 'ada', 'ada', 'ada5', $user->getEmail(), $user->getIdentifyProviderId());

    $url = $_SERVER['REQUEST_URI'];
    $urlparts = explode('/', $url);
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {
        if (sizeof($urlparts) == 7 && $urlparts[4] == 'user' && $urlparts[5] == 'search') {
            $idpid = $urlparts[3];
            $idp = $db->getIdentityProvider($idpid);
            $users = $db->searchInternalUsers($urlparts[6]);
            $json = "[";
            $first = true;
            foreach($users as $user) {
                $json .= ($first ? '' : ',') . $user->toJson();
                $first = false;
            }
            $json .= ']';
            echo $json;
        }
    }

?>