<?php
    class MySql implements Db {
        private $host;
        private $user;
        private $password;
        private $dbname;
        private $userId;
        private $identityProviderId;

        public function __construct($host, $user, $password, $dbname, $userid = null, $identityproviderid = null) {
            $this->host = $host;
            $this->user = $user;
            $this->password = $password;
            $this->dbname = $dbname;
            $this->userId = $userid;
            $this->identityProviderId = $identityproviderid;
        }
        
        public function getStores() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rights = $conn->query("select s.id, s.name, gr.level from stores s inner join grantedrights gr on s.id = gr.targetid where (gr.granteeid = 'everyone' or (gr.granteeid = '" . $this->userId . "' and gr.identityproviderid='" . $this->identityProviderId . "')) and gr.targettype='store' order by gr.weight desc");
                $potentials = [];
                $potentialIds = [];
                while ($row = $rights->fetch_object()) {
                    if (!array_key_exists($row->id, $potentialIds)) {
                        $potentials[sizeof($potentials)] = ["store"=> new Store($row->id, $row->name), "level"=>$row->level];
                        $potentialIds[$row->id] = true;
                    }
                }
                $readRight = $conn->query("select level from rights where systemright='read'");
                if ($right = $readRight->fetch_object()) {
                    $stores = [];
                    foreach($potentials as $potential) {
                        if (intval($right->level) & intval($potential["level"])) {
                            $stores[sizeof($stores)] = $potential["store"];
                        }
                    }
                    return $stores;
                }
                else
                    return [];
            }
            finally {
                $conn->close();
            }
        }

        public function getStore($id) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $storeresult = $conn->query("select s.id, s.name, gr.level from stores s inner join grantedrights gr on s.id = gr.targetid where s.id='" . $id . "' and gr.granteeid = '" . $this->userId . "' and gr.identityproviderid='" . $this->identityProviderId . "' and gr.targettype='store' order by gr.weight asc limit 1");
                if ($storeresult) {
                    if ($row = $storeresult->fetch_object()) {
                        $readRight = $conn->query("select level from rights where systemright='read'");
                        if ($right = $readRight->fetch_object()) {
                            if (intval($row->level) & intval($right->level)) {
                                return new Store($row->id, $row->name);
                            }
                            else
                                return false;
                        }
                        else
                            return false;
                    }
                    else
                        return false;
                }
                else
                    return false;
            }
            finally {
                $conn->close();
            }        
        }

        public function getIdentifyProviders() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $idps = $conn->query("select id,name, `type` from identityproviders");
                $results = [];
                while ($idp = $idps->fetch_object()) {
                    $results[sizeof($results)] = new IdentityProvider($idp->id, $idp->name, $idp->type);
                }
                return $results;
            }
            finally {
                $conn->close();
            }
        }

        public function getInternalUser($mail, $passwordhash, $idpid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $users = $conn->query("select id,firstname,lastname from users where email='" . $mail ."' and passwordhash='" . $passwordhash ."'");
                if ($user = $users->fetch_object()) {
                    return new User($user->id, $user->email, $user->firstname, $user->lastname, $idpid);
                }
                else {
                    return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function isStoreNameUnique($name) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $results = $conn->query("select id from stores where name='" . $name . "'");
                return $results->num_rows == 0;
            }
            finally {
                $conn->close();
            }
        }

        public function isUniqueClassName($storeid, $classname) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $results = $conn->query("select id from classes where name='" . $classname . "' and storeid = '" . $storeid . "'");
                return $results->num_rows == 0;
            }
            finally {
                $conn->close();
            }
        }
        public function canRead($targettype, $targetid=null) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                if (is_null($targetid)) {
                    $results = $conn->query("select level from grantedrights where (granteeid = 'everyone' or (granteeid = '" . $this->userId . "' and identityproviderid = '" . $this->identityProviderId . "')) and targettype = '" . $targettype . "' order by weight desc limit 1");
                }
                else {
                    $results = $conn->query("select level from grantedrights where (granteeid = 'everyone' or (granteeid = '" . $this->userId . "' and identityproviderid = '" . $this->identityProviderId . "')) and targettype = '" . $targettype . "' and targetid='" . $targetid . "' order by weight desc limit 1");
                }
                if ($results->num_rows == 0)
                    return false;
                else {
                    $rights = $conn->query("select level from rights where systemright='read'");
                    if ($rights->num_rows == 1) 
                        return intval($rights->fetch_object()->level) & intval($results->fetch_object()->level);
                    else
                        return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function canCreate($targettype, $targetid=null) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                if (is_null($targetid)) {
                    $results = $conn->query("select level from grantedrights where (granteeid = 'everyone' or (granteeid = '" . $this->userId . "' and identityproviderid = '" . $this->identityProviderId . "')) and targettype = '" . $targettype . "' order by weight desc limit 1");
                }
                else {
                    $results = $conn->query("select level from grantedrights where (granteeid = 'everyone' or (granteeid = '" . $this->userId . "' and identityproviderid = '" . $this->identityProviderId . "')) and targettype = '" . $targettype . "' and targetid='" . $targetid . "' order by weight desc limit 1");
                }
                if ($results->num_rows == 0)
                    return false;
                else {
                    $rights = $conn->query("select level from rights where systemright='create'");
                    if ($rights->num_rows == 1) 
                        return intval($rights->fetch_object()->level) & intval($results->fetch_object()->level);
                    else
                        return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function canCreateStore() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $results = $conn->query("select level from grantedrights where (granteeid = 'everyone' or (granteeid = '" . $this->userId . "' and identityproviderid = '" . $this->identityProviderId . "')) and targettype = 'domain' and targetid IS NULL order by weight desc limit 1");
                if ($results->num_rows == 0)
                    return false;
                else {
                    $rights = $conn->query("select level from rights where systemright='createstore'");
                    if ($rights->num_rows == 1) 
                        return intval($rights->fetch_object()->level) & intval($results->fetch_object()->level);
                    else
                        return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function canCreateClass($storeid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $results = $conn->query("select level from grantedrights where (granteeid = 'everyone' or (granteeid = '" . $this->userId . "' and identityproviderid = '" . $this->identityProviderId . "')) and targettype = 'store' and targetid='" . $storeid . "' order by weight desc limit 1");
                if ($results->num_rows == 0)
                    return false;
                else {
                    $rights = $conn->query("select level from rights where systemright='createclass'");
                    if ($rights->num_rows == 1) 
                        return intval($rights->fetch_object()->level) & intval($results->fetch_object()->level);
                    else
                        return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function createStore($name) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                $id = $idquery->fetch_object()->newid;
                $conn->query("insert into stores (id, name) values ('" . $id . "','" . $name . "')");
                $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) (select granteeid, identityproviderid,'" . $id . "',targettype, level, weight from grantedrights where targettype = 'store' and targetid IS NULL)");
                return $id;
            }
            finally {
                $conn->close();
            }
        }
        public function createClass($storeid, $class) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                $id = $idquery->fetch_object()->newid;
                $conn->query("insert into classes (id, name, creator, creatoridentityproviderid, folderclass, contentclass, storeid) values ('" . $id . "','" . $class->getName() . "','" . $this->userId . "','" . $this->identityProviderId . "'," . ($class->isFolderClass() ? '1' : '0') . "," . ($class->isDocumentClass() ? '1': '0') . ",'" . $storeid . "')");

                foreach($class->getProperties() as $property) {
                    $conn->query("insert into classproperties (id, name, `type`, required, multiple, classid) values (UUID(), '" . $property->getName() . "','" . $property->getType() . "'," . ($property->isRequired()? 1:0) . "," . ($property->isMultiple()? 1: 0). ",'" . $id . "')");
                }

                if (sizeof($class->getRights()) == 0) {
                    $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) values ('" . $this->userId . "','" . $this->identityProviderId . "','". $id . "','class',(select sum(level) from rights where classright = 1), 1)");
                }
                return $id;
            }
            finally {
                $conn->close();
            }
        }

    }