import styles from './Footer.module.css';
import FooterContent from "./FooterContent";

export default function Footer() {
  return (
    <div className={styles.footer}>
      <FooterContent />
    </div>
  );
}