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
                $storeresult = $conn->query("select s.id, s.name, gr.level from stores s inner join grantedrights gr on s.id = gr.targetid where s.id='" . $id . "' and (gr.granteeid = 'everyone' or (gr.granteeid = '" . $this->userId . "' and gr.identityproviderid='" . $this->identityProviderId . "')) and gr.targettype='store' order by gr.weight asc limit 1");
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
                    return new User($user->id, $mail, $user->firstname, $user->lastname, $idpid);
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

        public function canDeleteStore($storeid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $results = $conn->query("select level from grantedrights where (granteeid = 'everyone' or (granteeid = '" . $this->userId . "' and identityproviderid = '" . $this->identityProviderId . "')) and targettype = 'store' and targetid='" . $storeid . "' order by weight desc limit 1");
                if ($results->num_rows == 0)
                    return false;
                else {
                    $rights = $conn->query("select level from rights where systemright='delete'");
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

        public function createStore($name, $grantedrights, $addons) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                $id = $idquery->fetch_object()->newid;
                $conn->begin_transaction();
                try {
                    $succeeded = true;
                    $succeeded = $conn->query("insert into stores (id, name) values ('" . $id . "','" . $name . "')");
                    if ($succeeded) {
                        if (sizeof($grantedrights) > 0) {
                            foreach($grantedrights as $right) {
                                if ($right->grantee == 'everyone') {
                                    $succeeded = $conn->query("insert into grantedrights (granteeid, targetid, targettype, level, weight) values ('everyone', '" . $id . "','store'," . $right->level . ",0)");
                                }
                                else {
                                    $userrows = $conn->query("select id from users where email='" . $right->grantee . "'");
                                    if ($userrows->num_rows == 1) {
                                        $userrow = $userrows->fetch_object();
                                        $succeeded = $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) (select '" . $userrow->id . "',id,'" . $id . "','store', " . $right->level . ",1 from identityproviders where type='internal')");
                                    }
                                    else
                                        $succeeded = false;
                                }
                                if (!$succeeded)
                                    break;
                            }
                        }
                        else {
                            $succeeded = $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) (select granteeid, identityproviderid,'" . $id . "',targettype, level, weight from grantedrights where targettype = 'store' and targetid IS NULL)");
                            if ($succeeded) {
                                $countres = $conn->query("select count(*) countedrights from grantedrights where targetid = '" . $id . "'");
                                $rightscount = $countres->fetch_object()->countedrights;
                                if ($rightscount == 0) {
                                    $succeeded = $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) values ('" . $this->userId . "', '" . $this->identityProviderId . "','" . $id . "','store', (select sum(level) from rights where storeright=1), 1)");
                                }
                            }
                        }
                        if ($addons) {
                            foreach($addons as $addon) {
                                $jsons = $conn->query("select structure from addons where id='" . $addon . "'");
                                if ($json = $jsons->fetch_object()) {
                                    $definition = json_decode($json->structure);
                                    foreach($definition->classes as $classdefinition) {
                                        $class = AdaClass::fromAddOn($classdefinition);
                                        if ($classdefinition->security) {
                                            $rights = $this->getRights();
                                            foreach($classdefinition->security as $sec) {
                                                $level = 0;
                                                foreach($sec->rights as $right) {
                                                    foreach($rights as $checkright) {
                                                        if ($checkright->getName() == $right || $checkright->getSystemRight() == $right)
                                                            $level += $checkright->getLevel();
                                                    }
                                                }
                                            }
                                            if ($sec->grantee == 'everyone') {
                                                $right = new GrantedRight('everyone', null, $level, 0);
                                                $class->addRight($right);
                                            }
                                            else {
                                                $right = new GranteeRight($sec->grantee, $sec->identityprovider, $level, 1);
                                                $class->addRight($right);
                                            }
                                        }
                                        $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                                        $classid = $idquery->fetch_object()->newid;
                                        $conn->query("insert into classes (id, name, creator, creatoridentityproviderid, folderclass, contentclass, storeid) values ('" . $classid . "','" . $class->getName() . "','" . $this->userId . "','" . $this->identityProviderId . "'," . ($class->isFolderClass() ? '1' : '0') . "," . ($class->isDocumentClass() ? '1': '0') . ",'" . $id . "')");
                        
                                        foreach($class->getProperties() as $property) {
                                            $conn->query("insert into classproperties (id, name, `type`, required, multiple, classid) values (UUID(), '" . $property->getName() . "','" . $property->getType() . "'," . ($property->isRequired()? 1:0) . "," . ($property->isMultiple()? 1: 0). ",'" . $classid . "')");
                                        }
                        
                                        if (sizeof($class->getRights()) == 0) {
                                            $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) values ('" . $this->userId . "','" . $this->identityProviderId . "','". $classid . "','class',(select sum(level) from rights where classright = 1), 1)");
                                        }
                                        else {
                                            foreach($class->getRights() as $right) {
                                                if ($right->getGranteeId() == 'everyone') 
                                                    $conn->query("insert into grantedrights (granteeid, targetid, targettype, level, weight) values ('everyone', '" . $classid . "','class'," . $right->getLevel() . ",0)");
                                                else {
                                                    $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) values ('" . $right->getGranteeId() . "','" . $right->getIdentityProviderId() . "','" . $classid . "','class'," . $right->getLevel() . ",0)");
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    $succeeded = false;
                                }
                            }
                        }
                    }
                    if ($succeeded) {
                        $conn->commit();
                        return $id;
                    }
                    else
                        $conn->rollback();
                }
                catch (Exception $err) {
                    $conn->rollback();
                }
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
                else {
                    foreach($class->getRights() as $right) {
                        if ($right->getGranteeId() == 'everyone') 
                            $conn->query("insert into grantedrights (granteeid, targetid, targettype, level, weight) values ('everyone', '" . $id . "','class'," . $right->getLevel() . ",0)");
                        else {
                            $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) values ('" . $right->getGranteeId() . "','" . $right->getIdentityProviderId() . "','" . $id . "','class'," . $right->getLevel() . ",0)");
                        }
                    }
                }
                return $id;
            }
            finally {
                $conn->close();
            }
        }

        public function areValidRights($rights) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $checked = [];
                $result = true;
                foreach($rights as $right) {
                    if ($right->grantee != 'everyone') {
                        $results = $conn->query("select id,name from identityproviders where id = '". $right->identityprovider . "' or name = '" . $right->identityprovider . "'");
                        if ($results->num_rows == 0) {
                            $result = false;
                            break;
                        }
                        else {
                            $idp = $results->fetch_object();
                            if ($idp->name == $right->identityprovider)
                                $right->identityprovider = $idp->id;
                        }
                    }
                }
                return $result;
            }
            finally {
                $conn->close();
            }
        }

        public function getClasses($storeid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $potentialrows = $conn->query("select c.id, c.name, c.folderclass, c.contentclass, gr.level from classes c inner join grantedrights gr on c.id = gr.targetid where (gr.granteeid = 'everyone' or (gr.granteeid = '" . $this->userId . "' and gr.identityproviderid = '" . $this->identityProviderId . "')) and c.storeid = '" . $storeid . "' order by gr.weight desc");
                $potentials = [];
                $potentialIds = [];
                while ($potentialrow = $potentialrows->fetch_object()) {
                    if (!array_key_exists($potentialrow->id, $potentialIds)) {
                        $newpotential = new AdaClass($potentialrow->id, $potentialrow->name);
                        $newpotential->setIsDocumentClass($potentialrow->documentclass == 1);
                        $newpotential->setIsFolderClass($potentialrow->folderclass == 1);
                        $potentials[sizeof($potentials)] = ["class" => $newpotential, "level" => $potentialrow->level];
                        $potentialIds[$potentialrow->id] = true;
                    }
                }
                $readRight = $conn->query("select level from rights where systemright='read'");
                if ($right = $readRight->fetch_object()) {
                    $classes = [];
                    foreach($potentials as $potential) {
                        if (intval($right->level) & intval($potential["level"])) {
                            $classes[sizeof($classes)] = $potential["class"];
                        }
                    }
                    return $classes;
                }
                else
                    return [];
            }
            finally {
                $conn->close();
            }
        }

        public function getClass($storeid, $classid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rows = $conn->query("select c.id, c.name, c.folderclass, c.contentclass, gr.level from classes c inner join grantedrights gr on c.id = gr.targetid where c.storeid = '" . $storeid . "' and c.id = '" . $classid . "' and (gr.granteeid = 'everyone' or (gr.granteeid = '" . $this->userId . "' and gr.identityproviderid = '" . $this->identityProviderId . "')) and gr.targettype='class' order by gr.weight asc limit 1");
                if ($rows) {
                    $row = $rows->fetch_object();
                    $readRight = $conn->query("select level from rights where systemright='read'");
                    if ($readRight) {
                        if ($right = $readRight->fetch_object()) {
                            if (intval($row->level) & intval($right->level)) {
                                $newClass = new AdaClass($row->id, $row->name);
                                $newClass->setIsFolderClass($row->folderclass == 1);
                                $newClass->setIsDocumentClass($row->documentclass == 1);

                                $props = $conn->query("select id, name, `type`, required, multiple from classproperties where classid = '" . $classid . "'");
                                if ($props) {
                                    while ($prop = $props->fetch_object()) {
                                        $newProp = new Property($prop->id, $prop->name, $prop->type);
                                        $newProp->setMultiple($prop->multiple == 1);
                                        $newProp->setRequired($prop->required == 1);
                                        $newClass->addProperty($newProp);
                                    }
                                }
                                return $newClass;
                            }
                            else
                                return false;
                        }
                        else {
                            return false;
                        }
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

        public function getRights() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rows = $conn->query("select id, name, systemright, level, domainright, storeright, classright, objectright from rights");
                $rights = [];
                while ($row = $rows->fetch_object()) {
                    $rights[sizeof($rights)] = new Right($row->id, $row->name, $row->systemright, $row->level, $row->domainright, $row->storeright, $row->classright, $row->objectright);
                }
                return $rights;
            }
            finally {
                $conn->close();
            }
        }

        public function getIdentityProviders() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rows = $conn->query("select id, name, `type` from identityproviders");
                $providers = [];
                while ($row = $rows->fetch_object()) {
                    $providers[sizeof($providers)] = new IdentityProvider($row->id, $row->name, $row->type);
                }
                return $providers;
            }
            finally {
                $conn->close();
            }
        }

        public function deleteStore($storeid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                return $conn->query("delete from stores where id = '" . $storeid . "'");
            }
            finally {
                $conn->close();
            }
        }

        public function canAddAddon() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $systemrights = $conn->query("select level from rights where systemright='createaddon'");
                if ($systemright = $systemrights->fetch_object()) {
                    $grantedrights = $conn->query("select level from grantedrights where targettype='domain' order by weight limit 1");
                    if ($grantedright = $grantedrights->fetch_object()) {
                        return intval($systemright->level & intval($grantedright->level));
                    }
                    else return false;
                }
                else
                    return false;
            }
            finally {
                $conn->close();
            }
        }

        public function addAddon($id, $name, $json) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $checks = $conn->query("select id from addons where id = '" . $id . "'");
                if ($check = $checks->fetch_object())
                    throw new Exception("Addon with id '" . $id . "' already exists");
                else {
                    return $conn->query("insert into addons (id, name, structure) values ('" . $id . "','" . $name . "','" . $conn->real_escape_string($json) . "')");
                }
            }
            finally {
                $conn->close();
            }

        }

        public function canCreateObject($classid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $results = $conn->query("select level from grantedrights where (granteeid = 'everyone' or (granteeid = '" . $this->userId . "' and identityproviderid = '" . $this->identityProviderId . "')) and targettype = 'class' and targetid='" . $classid . "' order by weight desc limit 1");
                if ($results->num_rows == 0)
                    return false;
                else {
                    $rights = $conn->query("select level from rights where systemright='createobject'");
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

       public function createObject($storeid, $class, $request) {
            try {
                $detailErrorMessage = 'Object creation failed';
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                $id = $idquery->fetch_object()->newid;
                $succeeded = true;
                $conn->begin_transaction();
                $succeeded = $conn->query("insert into objects (id, classid, storeid, creator, creatoridentityproviderid) values ('" . $id . "','" . $class->getId() . "','" . $storeid . "','" . $this->userId . "','" . $this->identityProviderId . "')");
                if ($succeeded) {
                    if (gettype($request->rights) == "NULL" or sizeof($request->rights) == 0) {
                        $succeeded = $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) values ('" . $this->userId . "','" . $this->identityProviderId . "','". $id . "','object',(select sum(level) from rights where objectright = 1), 1)");
                    }
                    else {
                        foreach($request->rights as $right) {
                            if ($succeeded) {
                                if ($right->grantee == 'everyone') 
                                    $succeeded = $conn->query("insert into grantedrights (granteeid, targetid, targettype, level, weight) values ('everyone', '" . $id . "','object'," . $right->level . ",0)");
                                else {
                                    $succeeded = $conn->query("insert into grantedrights (granteeid, identityproviderid, targetid, targettype, level, weight) values ('" . $right->grantee . "','" . $right->identityprovider . "','" . $id . "','object'," . $right->level . ",0)");
                                }
                            }
                        }
                    }
                    if ($succeeded) {
                        if (gettype($request->properties) == 'object') {
                            $propertyNames = array_keys( get_object_vars($request->properties));
                            foreach($propertyNames as $property) {
                                $propertyPmrops = $conn->query("select id, type from classproperties where classid = '" . $class->getId() . "' and name ='" . $property . "'");
                                if ($propertyProps->num_rows == 1) {
                                    $props = $propertyProps->fetch_object();
                                    $type = $props->type;
                                    $propid = $props->id;
                                    if ($type == 'string') {
                                        $succeeded = $conn->query("insert into objectproperties (objectid,propertyid,string_value) values ('" . $id . "','" . $propid . "','" . $request->properties->$property . "')");
                                    }
                                    else {
                                        $succeeded = false;
                                        $detailErrorMessage = "Unsupported property type (" . $type . ")";
                                    }
                                }
                                else {
                                    $succeeded = false;
                                    $detailErrorMessage = "Unknown property (" . $property . ")";
                                }
                                if (!$succeeded)
                                    break;
                            }
                        }
                        if ($succeeded) {
                            $conn->commit();
                            return new AdaObject($id, $class);
                        }
                        else {
                            $conn->rollback();
                            throw new Exception($detailErrorMessage);
                        }
                    }
                    else {
                        $conn->rollback();
                        throw new Exception($detailErrorMessage);
                    }
                }
                else {
                    $conn->rollback();
                    throw new Exception($detailErrorMessage);
                }
            }
            finally {
                $conn->close();
            }
       }
}