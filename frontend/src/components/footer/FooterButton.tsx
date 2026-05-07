import styles from './Footer.module.css';

interface Props {
  text: string;
  icon: string;
  onClick?: () => void;
}

export default function FooterButton({ text, icon, onClick }: Props) {
  return (
    <button className={styles.footerBtn} onClick={onClick}>
      <img src={icon} className={styles.footerIcon} />
      <span>{text}</span>
    </button>
  );
}