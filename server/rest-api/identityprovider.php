<?php
    include('db.php');
    include('mysql.php');
    include('model.php');
    include('auth.php');
    include('settings.php');

    $auth = new Auth($_settings);
    if (!$user = $auth->isAuthorized()) {
        header("HTTP/1.1 401 Unauthorized");
        exit;
    }

    $db = new MySql($_settings['dbhost'], $_settings['dbuser'], $_settings['dbpassword'], $_settings['dbname'], $user->getEmail(), $user->getIdentifyProviderId(), $_settings['contentdir']);

    $url = $_SERVER['REQUEST_URI'];
    $urlparts = explode('/', $url);
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {
        if (sizeof($urlparts) == 7 && $urlparts[4] == 'user' && $urlparts[5] == 'search') {
            $idpid = $urlparts[3];
            $idp = $db->getIdentityProvider($idpid);
            if ($idp->getType() == 'internal') {
                $users = $db->searchInternalUsers($urlparts[6]);
            }
            else if ($idp->getType() == 'oauth') {
                $users = $idp->searchUsers($urlparts[6]);
            }
            else {
                $user = [];
            }
            $json = "[";
            $first = true;
            foreach($users as $user) {
                $json .= ($first ? '' : ',') . $user->toJson();
                $first = false;
            }
            $json .= ']';
            echo $json;
        }
        else if (sizeof($urlparts) == 5 && $urlparts[4] == 'roles') {
            $idpid = $urlparts[3];
            $idp = $db->getIdentityProvider($idpid);
            if ($idp->getType() == 'oauth') {
                $roles = $idp->getRoles();
            }
            else {
                $roles = [];
            }
            echo $roles;
        }
        else if (sizeof($urlparts) == 6 && $urlparts[4] == 'user') {
            $idpid = $urlparts[3];
            $idp = $db->getIdentityProvider($idpid);
            $userId = $urlparts[5];
            if ($idp->getType() == 'oauth') {
                echo $idp->getOAuthUser($userId)->toJson();
            }
            else if ($idp->getType() == 'internal') {
                echo $db->getInternalUserById($userId)->toJson();
            }
        }
        else {
            print_r($urlparts);
        }
    }

?>