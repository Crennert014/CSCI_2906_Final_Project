<?php

class fileOpenException extends Exception
{
  function __toString(): string
  {
       return "fileOpenException ". $this->getCode()
              . ": ". $this->getMessage()."<br />"." in "
              . $this->getFile(). " on line ". $this->getLine()
              . "<br />";
   }
}

class fileWriteException extends Exception
{
  function __toString(): string
  {
       return "fileWriteException ". $this->getCode()
              . ": ". $this->getMessage()."<br />"." in "
              . $this->getFile(). " on line ". $this->getLine()
              . "<br />";
   }
}

class fileLockException extends Exception
{
  function __toString(): string
  {
       return "fileLockException ". $this->getCode()
              . ": ". $this->getMessage()."<br />"." in "
              . $this->getFile(). " on line ". $this->getLine()
              . "<br />";
   }
}

?>
